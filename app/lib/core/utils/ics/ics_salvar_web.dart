// Implementacao web (PWA): dispara o download via Blob.
// ignore: avoid_web_libraries_in_flutter, deprecated_member_use
import 'dart:html' as html;

const bool exportacaoIcsSuportada = true;

Future<void> salvarArquivoIcs(String conteudo, String nomeArquivo) async {
  final blob = html.Blob([conteudo], 'text/calendar');
  final url = html.Url.createObjectUrlFromBlob(blob);
  html.AnchorElement(href: url)
    ..setAttribute('download', nomeArquivo)
    ..click();
  html.Url.revokeObjectUrl(url);
}
