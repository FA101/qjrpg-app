import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import 'auth_providers.dart';

String? _dataParaIso(String dataBr) {
  final partes = dataBr.trim().split('/');
  if (partes.length != 3) return null;
  final dia = partes[0].padLeft(2, '0');
  final mes = partes[1].padLeft(2, '0');
  final ano = partes[2];
  return '$ano-$mes-$dia';
}

int? _idadeCalculada(String dataBr) {
  final iso = _dataParaIso(dataBr);
  if (iso == null) return null;
  try {
    final nascimento = DateTime.parse(iso);
    final hoje = DateTime.now();
    int idade = hoje.year - nascimento.year;
    if (hoje.month < nascimento.month || (hoje.month == nascimento.month && hoje.day < nascimento.day)) {
      idade--;
    }
    return idade;
  } catch (_) {
    return null;
  }
}

class LoginPage extends ConsumerStatefulWidget {
  const LoginPage({super.key});

  @override
  ConsumerState<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends ConsumerState<LoginPage> {
  final _emailController = TextEditingController();
  final _codigoController = TextEditingController();
  final _nomeController = TextEditingController();
  final _celularController = TextEditingController();
  final _apelidoController = TextEditingController();
  final _dataNascimentoController = TextEditingController();
  final _responsavelEmailController = TextEditingController();
  final _totpController = TextEditingController();
  final _focusCodigo = FocusNode();
  final _focusTotp = FocusNode();

  String? _sexo;
  bool _codigoEnviado = false;
  bool _totpPendente = false;
  bool _carregando = false;
  String? _erro;
  String? _codigoDeTeste;

  bool get _ehMenorDeIdade {
    final idade = _idadeCalculada(_dataNascimentoController.text);
    return idade != null && idade < 18;
  }

  Future<void> _solicitarCodigo() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      final codigo = await ref.read(authNotifierProvider.notifier).solicitarCodigo(_emailController.text.trim());
      setState(() { _codigoEnviado = true; _codigoDeTeste = codigo; });
      Future.delayed(const Duration(milliseconds: 100), () => _focusCodigo.requestFocus());
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  Future<void> _confirmarCodigo() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      final pendente = await ref.read(authNotifierProvider.notifier).confirmarCodigo(
            _emailController.text.trim(),
            _codigoController.text.trim(),
            nome: _nomeController.text.trim().isEmpty ? null : _nomeController.text.trim(),
            celular: _celularController.text.trim().isEmpty ? null : _celularController.text.trim(),
            apelido: _apelidoController.text.trim().isEmpty ? null : _apelidoController.text.trim(),
            dataNascimento: _dataParaIso(_dataNascimentoController.text),
            sexo: _sexo,
            responsavelEmail: _responsavelEmailController.text.trim().isEmpty
                ? null
                : _responsavelEmailController.text.trim(),
          );
      if (pendente) {
        setState(() => _totpPendente = true);
        Future.delayed(const Duration(milliseconds: 100), () => _focusTotp.requestFocus());
      }
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  Future<void> _validarTotp() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      await ref.read(authNotifierProvider.notifier).validarSegundoFator(
          _emailController.text.trim(), _totpController.text.trim());
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_totpPendente) {
      return Scaffold(
        appBar: AppBar(title: const Text('Verificacao em duas etapas')),
        body: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text('Digite o codigo do seu aplicativo autenticador.'),
              const SizedBox(height: 16),
              TextField(
                controller: _totpController,
                focusNode: _focusTotp,
                decoration: const InputDecoration(labelText: 'Codigo do autenticador'),
                keyboardType: TextInputType.number,
                onSubmitted: (_) => _validarTotp(),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _carregando ? null : _validarTotp,
                child: Text(_carregando ? 'Validando...' : 'Validar'),
              ),
              if (_erro != null) ...[
                const SizedBox(height: 16),
                SelectableText(_erro!, style: const TextStyle(color: Colors.red)),
              ],
            ],
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Entrar - Quero Jogar RPG')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _emailController,
              enabled: !_codigoEnviado,
              decoration: const InputDecoration(labelText: 'E-mail'),
              keyboardType: TextInputType.emailAddress,
              onSubmitted: (_) { if (!_codigoEnviado) _solicitarCodigo(); },
            ),
            const SizedBox(height: 16),
            if (!_codigoEnviado)
              ElevatedButton(
                onPressed: _carregando ? null : _solicitarCodigo,
                child: Text(_carregando ? 'Enviando...' : 'Enviar codigo'),
              ),
            if (_codigoEnviado) ...[
              if (_codigoDeTeste != null)
                Text('Modo desenvolvimento - codigo: $_codigoDeTeste',
                    style: const TextStyle(color: Colors.orange)),
              const SizedBox(height: 8),
              TextField(
                controller: _codigoController,
                focusNode: _focusCodigo,
                decoration: const InputDecoration(labelText: 'Codigo recebido'),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 8),
              const Text('Preencha abaixo so no seu primeiro acesso:', style: TextStyle(fontSize: 12)),
              TextField(
                controller: _apelidoController,
                decoration: const InputDecoration(labelText: 'Apelido publico', helperText: 'Minimo 3 caracteres, unico'),
              ),
              TextField(
                controller: _nomeController,
                decoration: const InputDecoration(labelText: 'Nome real (privado por padrao)'),
              ),
              TextField(
                controller: _celularController,
                decoration: const InputDecoration(labelText: 'Celular'),
              ),
              TextField(
                controller: _dataNascimentoController,
                decoration: const InputDecoration(labelText: 'Data de nascimento (dd/mm/aaaa)'),
                onChanged: (_) => setState(() {}),
              ),
              DropdownButtonFormField<String>(
                initialValue: _sexo,
                decoration: const InputDecoration(labelText: 'Sexo (opcional, so estatistico)'),
                items: const [
                  DropdownMenuItem(value: null, child: Text('Prefiro nao informar')),
                  DropdownMenuItem(value: 'M', child: Text('Masculino')),
                  DropdownMenuItem(value: 'F', child: Text('Feminino')),
                ],
                onChanged: (v) => setState(() => _sexo = v),
              ),
              if (_ehMenorDeIdade)
                TextField(
                  controller: _responsavelEmailController,
                  decoration: const InputDecoration(
                    labelText: 'E-mail do responsavel',
                    helperText: 'Obrigatorio para menores de 18 anos',
                  ),
                  onSubmitted: (_) => _confirmarCodigo(),
                ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _carregando ? null : _confirmarCodigo,
                child: Text(_carregando ? 'Confirmando...' : 'Confirmar e entrar'),
              ),
            ],
            if (_erro != null) ...[
              const SizedBox(height: 16),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(child: SelectableText(_erro!, style: const TextStyle(color: Colors.red))),
                  IconButton(
                    icon: const Icon(Icons.copy, size: 18),
                    tooltip: 'Copiar mensagem de erro',
                    onPressed: () {
                      Clipboard.setData(ClipboardData(text: _erro!));
                      ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Erro copiado para a area de transferencia.')));
                    },
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}
