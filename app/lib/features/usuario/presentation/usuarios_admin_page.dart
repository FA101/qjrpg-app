import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/tag_chip.dart';
import '../../auth/presentation/auth_providers.dart';
import '../../tag/domain/tag.dart';
import '../../tag/presentation/tag_providers.dart';

/// Resumo de usuario para a administracao (dados vindos de GET /api/usuarios).
class UsuarioResumo {
  final String id;
  final String nomeExibicao;
  final String email;
  final String papel;
  final bool ativo;

  const UsuarioResumo({required this.id, required this.nomeExibicao, required this.email,
      required this.papel, required this.ativo});

  factory UsuarioResumo.fromJson(Map<String, dynamic> j) => UsuarioResumo(
        id: j['id'] as String,
        nomeExibicao: j['nomeExibicao'] as String? ?? 'Usuario',
        email: j['email'] as String? ?? '',
        papel: j['papel'] as String,
        ativo: j['ativo'] as bool? ?? true,
      );
}

final usuariosAdminProvider = FutureProvider.autoDispose<List<UsuarioResumo>>((ref) async {
  final r = await DioClient.instance.get('/usuarios');
  return (r.data as List).map((j) => UsuarioResumo.fromJson(j as Map<String, dynamic>)).toList();
});

const _rotuloPapel = {'ADMIN': 'Administrador', 'MODERADOR': 'Moderador', 'COMUM': 'Usuario'};

/// Gestao de usuarios (RF22-RF26). Admin: papeis, ativacao e tags de todos.
/// Moderador: ativacao e tags apenas de usuarios comuns (o back-end tambem valida).
class UsuariosAdminPage extends ConsumerStatefulWidget {
  const UsuariosAdminPage({super.key});

  @override
  ConsumerState<UsuariosAdminPage> createState() => _UsuariosAdminPageState();
}

class _UsuariosAdminPageState extends ConsumerState<UsuariosAdminPage> {
  String _filtro = '';

  Future<void> _acao(Future<void> Function() fn, {String? sucesso, String? usuarioId}) async {
    try {
      await fn();
      ref.invalidate(usuariosAdminProvider);
      if (usuarioId != null) ref.invalidate(tagsDoUsuarioProvider(usuarioId));
      if (sucesso != null && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(sucesso)));
      }
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
    }
  }

  Future<bool> _confirmar(String titulo, String mensagem) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: Text(titulo),
        content: Text(mensagem),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('Cancelar')),
          ElevatedButton(onPressed: () => Navigator.pop(c, true), child: const Text('Confirmar')),
        ],
      ),
    );
    return ok == true;
  }

  Future<void> _alterarPapel(UsuarioResumo u, String papel) async {
    if (!await _confirmar('Alterar papel',
        'Alterar ${u.nomeExibicao} de ${_rotuloPapel[u.papel]} para ${_rotuloPapel[papel]}?')) return;
    await _acao(() => DioClient.instance.patch('/usuarios/${u.id}/papel', data: {'papel': papel}),
        sucesso: 'Papel atualizado.', usuarioId: u.id);
  }

  Future<void> _alterarAtivo(UsuarioResumo u) async {
    final acao = u.ativo ? 'Desativar' : 'Reativar';
    if (!await _confirmar('$acao conta', '$acao a conta de ${u.nomeExibicao}?')) return;
    await _acao(() => DioClient.instance.patch('/usuarios/${u.id}/ativo', data: {'ativo': !u.ativo}),
        sucesso: 'Conta atualizada.');
  }

  Future<void> _adicionarTag(UsuarioResumo u, List<Tag> atuais) async {
    final todas = await ref.read(tagRepositoryProvider).listarTodos();
    final idsAtuais = atuais.map((t) => t.id).toSet();
    final disponiveis = todas.where((t) => !idsAtuais.contains(t.id) && t.nome != 'Moderador').toList();
    if (!mounted) return;
    if (disponiveis.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Nao ha tags disponiveis.')));
      return;
    }
    final escolhida = await showDialog<Tag>(
      context: context,
      builder: (c) => SimpleDialog(
        title: Text('Adicionar tag a ${u.nomeExibicao}'),
        children: disponiveis
            .map((t) => SimpleDialogOption(onPressed: () => Navigator.pop(c, t), child: TagChip(tag: t)))
            .toList(),
      ),
    );
    if (escolhida == null) return;
    await _acao(() => ref.read(tagRepositoryProvider).atribuirAoUsuario(u.id, escolhida.id!),
        sucesso: 'Tag adicionada.', usuarioId: u.id);
  }

  Future<void> _removerTag(UsuarioResumo u, Tag t) async {
    if (!await _confirmar('Remover tag', 'Remover a tag ${t.nome} de ${u.nomeExibicao}?')) return;
    await _acao(() => ref.read(tagRepositoryProvider).removerDoUsuario(u.id, t.id!),
        sucesso: 'Tag removida.', usuarioId: u.id);
  }

  @override
  Widget build(BuildContext context) {
    final eu = ref.watch(authNotifierProvider);
    final souAdmin = eu?.ehAdmin ?? false;
    final usuariosAsync = ref.watch(usuariosAdminProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Usuarios')),
      drawer: const AppDrawer(),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: TextField(
            decoration: const InputDecoration(prefixIcon: Icon(Icons.search), labelText: 'Buscar por nome ou e-mail'),
            onChanged: (v) => setState(() => _filtro = v.trim().toLowerCase()),
          ),
        ),
        Expanded(
          child: usuariosAsync.when(
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text(extrairMensagemErro(e))),
            data: (usuarios) {
              final lista = usuarios.where((u) => _filtro.isEmpty
                  || u.nomeExibicao.toLowerCase().contains(_filtro)
                  || u.email.toLowerCase().contains(_filtro)).toList();
              if (lista.isEmpty) return const Center(child: Text('Nenhum usuario encontrado.'));
              return RefreshIndicator(
                onRefresh: () async => ref.invalidate(usuariosAdminProvider),
                child: ListView.builder(
                  itemCount: lista.length,
                  itemBuilder: (_, i) => _CartaoUsuario(
                    usuario: lista[i],
                    souAdmin: souAdmin,
                    souEu: lista[i].id == eu?.id,
                    onAlterarPapel: (p) => _alterarPapel(lista[i], p),
                    onAlterarAtivo: () => _alterarAtivo(lista[i]),
                    onAdicionarTag: (atuais) => _adicionarTag(lista[i], atuais),
                    onRemoverTag: (t) => _removerTag(lista[i], t),
                  ),
                ),
              );
            },
          ),
        ),
      ]),
    );
  }
}

class _CartaoUsuario extends ConsumerWidget {
  final UsuarioResumo usuario;
  final bool souAdmin;
  final bool souEu;
  final void Function(String papel) onAlterarPapel;
  final VoidCallback onAlterarAtivo;
  final void Function(List<Tag> atuais) onAdicionarTag;
  final void Function(Tag tag) onRemoverTag;

  const _CartaoUsuario({required this.usuario, required this.souAdmin, required this.souEu,
      required this.onAlterarPapel, required this.onAlterarAtivo, required this.onAdicionarTag,
      required this.onRemoverTag});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // RF25: Moderador so age sobre usuarios comuns.
    final podeGerir = !souEu && (souAdmin || usuario.papel == 'COMUM');
    final tagsAsync = ref.watch(tagsDoUsuarioProvider(usuario.id));

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Expanded(
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text(usuario.nomeExibicao, style: Theme.of(context).textTheme.titleMedium),
                Text(usuario.email, style: Theme.of(context).textTheme.bodySmall),
                Text('${_rotuloPapel[usuario.papel] ?? usuario.papel}${usuario.ativo ? '' : ' - DESATIVADO'}'),
              ]),
            ),
            if (podeGerir)
              PopupMenuButton<String>(
                tooltip: 'Acoes',
                onSelected: (v) {
                  if (v == 'ativo') {
                    onAlterarAtivo();
                  } else {
                    onAlterarPapel(v);
                  }
                },
                itemBuilder: (_) => [
                  if (souAdmin)
                    for (final p in ['ADMIN', 'MODERADOR', 'COMUM'])
                      if (p != usuario.papel)
                        PopupMenuItem(value: p, child: Text('Tornar ${_rotuloPapel[p]}')),
                  PopupMenuItem(value: 'ativo', child: Text(usuario.ativo ? 'Desativar conta' : 'Reativar conta')),
                ],
              ),
          ]),
          const SizedBox(height: 8),
          tagsAsync.when(
            loading: () => const LinearProgressIndicator(),
            error: (e, _) => Text(extrairMensagemErro(e)),
            data: (tags) => Wrap(spacing: 6, runSpacing: 6, children: [
              if (tags.isEmpty) const Text('Sem tags'),
              ...tags.map((t) => TagChip(
                    tag: t,
                    onRemover: podeGerir && t.nome != 'Moderador' ? () => onRemoverTag(t) : null,
                  )),
              if (podeGerir)
                ActionChip(
                  avatar: const Icon(Icons.add, size: 18),
                  label: const Text('Tag'),
                  tooltip: 'Adicionar tag',
                  onPressed: () => onAdicionarTag(tags),
                ),
            ]),
          ),
        ]),
      ),
    );
  }
}
