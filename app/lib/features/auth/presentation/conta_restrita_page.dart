import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';
import 'auth_providers.dart';

/// Tela unica para contas com acesso restrito:
/// - RF38: conta suspensa pelo proprio usuario (pode reativar aqui);
/// - RF35/RF36b: menor aguardando (ou com autorizacao suspensa pelo) responsavel.
class ContaRestritaPage extends ConsumerStatefulWidget {
  const ContaRestritaPage({super.key});

  @override
  ConsumerState<ContaRestritaPage> createState() => _ContaRestritaPageState();
}

class _ContaRestritaPageState extends ConsumerState<ContaRestritaPage> {
  bool _carregando = false;
  String? _erro;

  Future<void> _executar(Future<void> Function() acao) async {
    setState(() { _carregando = true; _erro = null; });
    try {
      await acao();
    } catch (e) {
      if (mounted) setState(() => _erro = extrairMensagemErro(e));
    } finally {
      if (mounted) setState(() => _carregando = false);
    }
  }

  Future<void> _reativar() => _executar(() async {
        await DioClient.instance.put('/usuarios/me/reativar');
        await ref.read(authNotifierProvider.notifier).recarregarPerfil();
      });

  Future<void> _verificarNovamente() =>
      _executar(() => ref.read(authNotifierProvider.notifier).recarregarPerfil());

  @override
  Widget build(BuildContext context) {
    final usuario = ref.watch(authNotifierProvider);
    final suspenso = usuario?.suspenso ?? false;

    return Scaffold(
      appBar: AppBar(title: const Text('Quero Jogar RPG')),
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 480),
          child: ListView(
            shrinkWrap: true,
            padding: const EdgeInsets.all(24),
            children: [
              Icon(suspenso ? Icons.pause_circle_outline : Icons.family_restroom, size: 64,
                  semanticLabel: suspenso ? 'Conta suspensa' : 'Aguardando responsavel'),
              const SizedBox(height: 16),
              Text(
                suspenso ? 'Sua conta esta suspensa' : 'Aguardando autorizacao do responsavel',
                style: Theme.of(context).textTheme.headlineSmall,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 12),
              Text(
                suspenso
                    ? 'Voce suspendeu sua conta. Enquanto isso, ela nao aparece para outros usuarios e '
                        'voce nao pode usar o app. Reative quando quiser voltar.'
                    : 'Contas de menores de 18 anos so podem ser usadas depois que o responsavel legal '
                        'informado no cadastro entrar no app com o proprio e-mail e autorizar o vinculo '
                        'em "Meu perfil". Se a autorizacao foi suspensa pelo responsavel, fale com ele.',
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              if (suspenso)
                ElevatedButton(
                  onPressed: _carregando ? null : _reativar,
                  child: const Text('Reativar minha conta'),
                )
              else
                ElevatedButton(
                  onPressed: _carregando ? null : _verificarNovamente,
                  child: const Text('Ja fui autorizado - verificar novamente'),
                ),
              if (_carregando) const Padding(
                padding: EdgeInsets.only(top: 16),
                child: Center(child: CircularProgressIndicator()),
              ),
              if (_erro != null) Padding(
                padding: const EdgeInsets.only(top: 16),
                child: Text(_erro!, style: TextStyle(color: Theme.of(context).colorScheme.error),
                    textAlign: TextAlign.center),
              ),
              const SizedBox(height: 16),
              TextButton(
                onPressed: () => ref.read(authNotifierProvider.notifier).sair(),
                child: const Text('Sair'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
