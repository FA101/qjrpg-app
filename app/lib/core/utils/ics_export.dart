import 'ics/ics_salvar_stub.dart'
    if (dart.library.js_interop) 'ics/ics_salvar_web.dart';

/// RF43: exporta um evento em formato iCalendar (RFC 5545).
/// A geracao do conteudo e pura (testavel); a entrega do arquivo depende da plataforma.
String gerarConteudoIcs({
  required String nome,
  required String data,
  required String local,
  String? horaInicio,
  String? horaFim,
  DateTime? agoraUtc,
  String? uid,
}) {
  final dataCompacta = data.replaceAll('-', '');
  String hora(String? h, String padrao) => (h ?? padrao).replaceAll(':', '').padRight(6, '0').substring(0, 6);
  final agora = (agoraUtc ?? DateTime.now().toUtc());
  final dtStamp = '${agora.toIso8601String().replaceAll(RegExp(r'[-:]'), '').split('.').first}Z';

  return [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//QJRPG//App//PT',
    'CALSCALE:GREGORIAN',
    'BEGIN:VEVENT',
    'UID:${uid ?? '${agora.microsecondsSinceEpoch}@qjrpg.app'}',
    'DTSTAMP:$dtStamp',
    'DTSTART:${dataCompacta}T${hora(horaInicio, '09:00:00')}',
    'DTEND:${dataCompacta}T${hora(horaFim, '22:00:00')}',
    'SUMMARY:${escaparTextoIcs(nome)}',
    'LOCATION:${escaparTextoIcs(local)}',
    'END:VEVENT',
    'END:VCALENDAR',
  ].join('\r\n');
}

/// Escapa caracteres especiais de TEXT conforme RFC 5545 (secao 3.3.11).
String escaparTextoIcs(String texto) => texto
    .replaceAll(r'\', r'\\')
    .replaceAll(';', r'\;')
    .replaceAll(',', r'\,')
    .replaceAll('\n', r'\n');

/// Indica se a plataforma atual consegue entregar o arquivo .ics.
bool get exportacaoIcsDisponivel => exportacaoIcsSuportada;

/// Gera e entrega o .ics (download no navegador/PWA).
Future<void> baixarIcsDoEvento({
  required String nome,
  required String data,
  required String local,
  String? horaInicio,
  String? horaFim,
}) {
  final conteudo = gerarConteudoIcs(
      nome: nome, data: data, local: local, horaInicio: horaInicio, horaFim: horaFim);
  return salvarArquivoIcs(conteudo, 'evento_qjrpg.ics');
}
