import 'package:flutter/material.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';

class RelatorioPage extends StatefulWidget {
  const RelatorioPage({super.key});

  @override
  State<RelatorioPage> createState() => _RelatorioPageState();
}

class _RelatorioPageState extends State<RelatorioPage> {
  final _tipoJogo = TextEditingController();
  final _mes = TextEditingController();
  final _ano = TextEditingController();
  final _faixaEtaria = TextEditingController();
  String? _sexo;

  List<dynamic> _mesas = [];
  Map<String, dynamic> _totalPorTipo = {};
  int _total = 0;
  bool _carregando = false;
  String? _erro;

  Future<void> _buscar() async {
    setState(() { _carregando = true; _erro = null; });
    try {
      final params = <String, dynamic>{};
      if (_tipoJogo.text.trim().isNotEmpty) params['tipoJogo'] = _tipoJogo.text.trim();
      if (_mes.text.trim().isNotEmpty) params['mes'] = _mes.text.trim();
      if (_ano.text.trim().isNotEmpty) params['ano'] = _ano.text.trim();
      if (_faixaEtaria.text.trim().isNotEmpty) params['faixaEtaria'] = _faixaEtaria.text.trim();
      if (_sexo != null) params['sexo'] = _sexo;

      final r = await DioClient.instance.get('/relatorios/mesas', queryParameters: params);
      setState(() {
        _mesas = r.data['mesas'] as List<dynamic>;
        _total = r.data['total'] as int;
        _totalPorTipo = Map<String, dynamic>.from(r.data['totalPorTipoJogo'] as Map);
      });
    } catch (e) {
      setState(() => _erro = extrairMensagemErro(e));
    } finally {
      setState(() => _carregando = false);
    }
  }

  @override
  void initState() {
    super.initState();
    _buscar();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Relatorios - Mesas')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Wrap(spacing: 8, runSpacing: 8, children: [
              SizedBox(width: 140, child: TextField(controller: _tipoJogo, decoration: const InputDecoration(labelText: 'Tipo de jogo'))),
              SizedBox(width: 80, child: TextField(controller: _mes, decoration: const InputDecoration(labelText: 'Mes'), keyboardType: TextInputType.number)),
              SizedBox(width: 80, child: TextField(controller: _ano, decoration: const InputDecoration(labelText: 'Ano'), keyboardType: TextInputType.number)),
              SizedBox(width: 120, child: TextField(controller: _faixaEtaria, decoration: const InputDecoration(labelText: 'Faixa etaria'))),
              SizedBox(
                width: 160,
                child: DropdownButtonFormField<String>(
                  initialValue: _sexo,
                  decoration: const InputDecoration(labelText: 'Sexo do mestre'),
                  items: const [
                    DropdownMenuItem(value: null, child: Text('Todos')),
                    DropdownMenuItem(value: 'M', child: Text('Masculino')),
                    DropdownMenuItem(value: 'F', child: Text('Feminino')),
                  ],
                  onChanged: (v) => setState(() => _sexo = v),
                ),
              ),
              ElevatedButton(onPressed: _carregando ? null : _buscar, child: const Text('Filtrar')),
            ]),
            const SizedBox(height: 16),
            if (_erro != null) SelectableText(_erro!, style: const TextStyle(color: Colors.red)),
            Text('Total: $_total mesas - por tipo: $_totalPorTipo'),
            const Divider(),
            Expanded(
              child: _carregando
                  ? const Center(child: CircularProgressIndicator())
                  : ListView(
                      children: _mesas.map((m) => ListTile(
                            title: Text('${m['eventoNome']} - ${m['tipoJogo']} (${m['sistemaJogo'] ?? ''})'),
                            subtitle: Text('Mestre: ${m['gameMasterNome']} - Data: ${m['eventoData']} - Vagas: ${m['vagasTotais']}'),
                          )).toList(),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
