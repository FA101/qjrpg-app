import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';
import '../../../core/widgets/tag_chip.dart';
import '../../tag/presentation/tag_providers.dart';
import 'auth_providers.dart';

class PerfilPage extends ConsumerStatefulWidget {
  const PerfilPage({super.key});

  @override
  ConsumerState<PerfilPage> createState() => _PerfilPageState();
}

class _PerfilPageState extends ConsumerState<PerfilPage> {
  late final TextEditingController _apelido;
  final _totpCodigo = TextEditingController();
  bool _mostrarNomeReal = false;
  bool _notificacoesAtivas = true;
  bool _suspenso = false;
  bool _totpAtivo = false;
  String? _totpSegredoPendente;
  String? _totpOtpauthUri;
  List<dynamic> _vinculos = [];

  @override
  void initState() {
    super.initState();
    final usuario = ref.read(authNotifierProvider);
    _apelido = TextEditingController(text: usuario?.apelido ?? '');
    _mostrarNomeReal = usuario?.mostrarNomeReal ?? false;
    _carregarPerfil();
    _carregarVinculos();
  }

  Future<void> _carregarPerfil() async {
    try {
      final r = await DioClient.instance.get('/usuarios/me');
      setState(() {
        _notificacoesAtivas = r.data['notificacoesPushAtivas'] as bool? ?? true;
        _suspenso = r.data['suspenso'] as bool? ?? false;
        _totpAtivo = r.data['totpAtivo'] as bool? ?? false;
      });
    } catch (_) {}
  }

  // RF36b/RF36c: menores vinculados a mim (autorizados ou nao).
  Future<void> _carregarVinculos() async {
    try {
      final r = await DioClient.instance.get('/usuarios/vinculos');
      if (mounted) setState(() => _vinculos = r.data as List<dynamic>);
    } catch (_) {}
  }

  void _mostrarMensagem(String texto) {
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(texto)));
  }

  void _mostrarErro(Object e) {
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
  }

  Future<void> _salvarPerfil() async {
    try {
      await DioClient.instance.put('/usuarios/me', data: {
        'apelido': _apelido.text.trim(),
        'mostrarNomeReal': _mostrarNomeReal,
      });
      _mostrarMensagem('Perfil atualizado.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _alternarNotificacoes(bool valor) async {
    try {
      await DioClient.instance.put('/usuarios/me/notificacoes', data: {'ativas': valor});
      setState(() => _notificacoesAtivas = valor);
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _suspenderConta() async {
    final dias = await showDialog<int>(
      context: context,
      builder: (dialogContext) {
        final controlador = TextEditingController(text: '30');
        return AlertDialog(
          title: const Text('Suspender conta'),
          content: TextField(
            controller: controlador,
            decoration: const InputDecoration(labelText: 'Por quantos dias (max 90)'),
            keyboardType: TextInputType.number,
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
            ElevatedButton(
              onPressed: () => Navigator.pop(dialogContext, int.tryParse(controlador.text.trim()) ?? 30),
              child: const Text('Suspender'),
            ),
          ],
        );
      },
    );
    if (dias == null) return;
    try {
      await DioClient.instance.put('/usuarios/me/suspender', data: {'dias': dias});
      setState(() => _suspenso = true);
      _mostrarMensagem('Conta suspensa por $dias dias.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _reativarConta() async {
    try {
      await DioClient.instance.put('/usuarios/me/reativar');
      setState(() => _suspenso = false);
      _mostrarMensagem('Conta reativada.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _excluirConta() async {
    final confirmar = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Excluir conta'),
        content: const Text(
            'Isso remove seus dados pessoais permanentemente (nome, apelido, celular, data de nascimento). '
            'Nao pode ser desfeito. Deseja continuar?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext, false), child: const Text('Cancelar')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            onPressed: () => Navigator.pop(dialogContext, true),
            child: const Text('Excluir definitivamente'),
          ),
        ],
      ),
    );
    if (confirmar != true) return;
    try {
      await DioClient.instance.delete('/usuarios/me');
      if (mounted) await ref.read(authNotifierProvider.notifier).sair();
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _confirmarVinculo(String menorId) async {
    try {
      await DioClient.instance.patch('/usuarios/vinculos/$menorId/confirmar');
      _carregarVinculos();
      _mostrarMensagem('Acesso autorizado.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  // RF36b: suspensao imediata, sem aprovacao de terceiros.
  Future<void> _suspenderAcessoDoMenor(String menorId) async {
    try {
      await DioClient.instance.patch('/usuarios/vinculos/$menorId/revogar');
      _carregarVinculos();
      _mostrarMensagem('Acesso do menor suspenso.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _iniciar2fa() async {
    try {
      final r = await DioClient.instance.post('/2fa/iniciar');
      setState(() {
        _totpSegredoPendente = r.data['segredo'] as String;
        _totpOtpauthUri = r.data['otpauthUri'] as String;
      });
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _confirmar2fa() async {
    try {
      await DioClient.instance.post('/2fa/confirmar', data: {'codigo': _totpCodigo.text.trim()});
      setState(() {
        _totpAtivo = true;
        _totpSegredoPendente = null;
        _totpOtpauthUri = null;
      });
      _mostrarMensagem('2FA ativado com sucesso.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  Future<void> _desativar2fa() async {
    try {
      await DioClient.instance.post('/2fa/desativar');
      setState(() => _totpAtivo = false);
      _mostrarMensagem('2FA desativado.');
    } catch (e) {
      _mostrarErro(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    final usuario = ref.watch(authNotifierProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Meu perfil')),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          Text('E-mail: ${usuario?.email ?? ''}'),
          const SizedBox(height: 12),
          const Text('Minhas tags', style: TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          ref.watch(minhasTagsProvider).when(
                loading: () => const LinearProgressIndicator(),
                error: (e, _) => Text(extrairMensagemErro(e)),
                data: (tags) => tags.isEmpty
                    ? const Text('Voce ainda nao tem tags.')
                    : Wrap(spacing: 6, runSpacing: 6, children: tags.map((t) => TagChip(tag: t)).toList()),
              ),
          const SizedBox(height: 16),
          TextField(controller: _apelido,
              decoration: const InputDecoration(labelText: 'Apelido publico', helperText: 'Minimo 3 caracteres, unico')),
          SwitchListTile(
            title: const Text('Mostrar meu nome real (em vez do apelido)'),
            value: _mostrarNomeReal,
            onChanged: (v) => setState(() => _mostrarNomeReal = v),
          ),
          ElevatedButton(onPressed: _salvarPerfil, child: const Text('Salvar')),
          const Divider(height: 32),
          SwitchListTile(
            title: const Text('Notificacoes push ativas'),
            value: _notificacoesAtivas,
            onChanged: _alternarNotificacoes,
          ),
          const Divider(height: 32),
          const Text('Autenticacao de dois fatores (2FA)', style: TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          if (_totpAtivo)
            OutlinedButton(
              style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
              onPressed: _desativar2fa,
              child: const Text('Desativar 2FA'),
            )
          else if (_totpSegredoPendente == null)
            ElevatedButton(onPressed: _iniciar2fa, child: const Text('Ativar 2FA'))
          else ...[
            const Text('Adicione manualmente no seu app autenticador (Google/Microsoft Authenticator):'),
            const SizedBox(height: 4),
            Row(children: [
              Expanded(child: SelectableText('Segredo: $_totpSegredoPendente')),
              IconButton(
                icon: const Icon(Icons.copy, size: 18),
                onPressed: () => Clipboard.setData(ClipboardData(text: _totpSegredoPendente!)),
              ),
            ]),
            SelectableText('URI: $_totpOtpauthUri', style: const TextStyle(fontSize: 11)),
            const SizedBox(height: 8),
            TextField(
              controller: _totpCodigo,
              decoration: const InputDecoration(labelText: 'Digite o codigo gerado para confirmar'),
              keyboardType: TextInputType.number,
              onSubmitted: (_) => _confirmar2fa(),
            ),
            const SizedBox(height: 8),
            ElevatedButton(onPressed: _confirmar2fa, child: const Text('Confirmar e ativar')),
          ],
          const Divider(height: 32),
          if (_vinculos.isNotEmpty) ...[
            const Text('Menores sob sua responsabilidade', style: TextStyle(fontWeight: FontWeight.bold)),
            ..._vinculos.map((v) {
              final autorizado = v['vinculoResponsavelConfirmado'] == true;
              return ListTile(
                title: Text((v['nomeExibicao'] ?? v['id']).toString()),
                subtitle: Text(autorizado ? 'Acesso autorizado' : 'Aguardando sua autorizacao'),
                trailing: autorizado
                    ? OutlinedButton(
                        onPressed: () => _suspenderAcessoDoMenor(v['id'] as String),
                        child: const Text('Suspender acesso'),
                      )
                    : ElevatedButton(
                        onPressed: () => _confirmarVinculo(v['id'] as String),
                        child: const Text('Autorizar'),
                      ),
              );
            }),
            const Divider(height: 32),
          ],
          Text('Status da conta: ${_suspenso ? "Suspensa" : "Ativa"}'),
          const SizedBox(height: 8),
          if (_suspenso)
            ElevatedButton(onPressed: _reativarConta, child: const Text('Reativar conta'))
          else
            OutlinedButton(onPressed: _suspenderConta, child: const Text('Suspender minha conta (ate 90 dias)')),
          const SizedBox(height: 8),
          OutlinedButton(
            style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
            onPressed: _excluirConta,
            child: const Text('Excluir minha conta permanentemente'),
          ),
        ],
      ),
    );
  }
}
