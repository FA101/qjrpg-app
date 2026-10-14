import 'package:app/core/utils/ics_export.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('gerarConteudoIcs (RF43)', () {
    final conteudo = gerarConteudoIcs(
      nome: 'QJRPG; Agosto, edicao 12',
      data: '2026-08-15',
      local: 'HUB Goias',
      horaInicio: '09:00:00',
      horaFim: '22:00',
      agoraUtc: DateTime.utc(2026, 8, 1, 12, 30, 45),
      uid: 'teste@qjrpg.app',
    );
    final linhas = conteudo.split('\r\n');

    test('usa CRLF e estrutura VCALENDAR/VEVENT valida', () {
      expect(linhas.first, 'BEGIN:VCALENDAR');
      expect(linhas.last, 'END:VCALENDAR');
      expect(linhas, containsAllInOrder(['BEGIN:VEVENT', 'END:VEVENT']));
      expect(linhas, contains('VERSION:2.0'));
    });

    test('formata datas e horas', () {
      expect(linhas, contains('DTSTART:20260815T090000'));
      expect(linhas, contains('DTEND:20260815T220000'));
      expect(linhas, contains('DTSTAMP:20260801T123045Z'));
    });

    test('escapa caracteres especiais do texto', () {
      expect(linhas, contains(r'SUMMARY:QJRPG\; Agosto\, edicao 12'));
    });
  });
}
