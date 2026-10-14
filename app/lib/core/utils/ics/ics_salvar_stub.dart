/// Plataformas nativas (Android/iOS/desktop): entrega do .ics ainda nao implementada
/// (exige um pacote de compartilhamento de arquivos). O botao fica oculto nelas.
const bool exportacaoIcsSuportada = false;

Future<void> salvarArquivoIcs(String conteudo, String nomeArquivo) async {
  throw UnsupportedError('Exportacao .ics ainda nao disponivel nesta plataforma');
}
