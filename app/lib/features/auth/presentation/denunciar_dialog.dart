import 'package:flutter/material.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/utils/erro_utils.dart';

Future<void> abrirDialogoDenuncia(BuildContext context, String usuarioDenunciadoId) {
  final motivo = TextEditingController();
  return showDialog(
    context: context,
    builder: (dialogContext) => AlertDialog(
      title: const Text('Denunciar apelido/comportamento'),
      content: TextField(
        controller: motivo,
        decoration: const InputDecoration(labelText: 'Motivo'),
        maxLines: 3,
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Cancelar')),
        ElevatedButton(
          onPressed: () async {
            try {
              await DioClient.instance.post('/denuncias', data: {
                'usuarioDenunciadoId': usuarioDenunciadoId,
                'motivo': motivo.text.trim(),
              });
              if (dialogContext.mounted) {
                Navigator.pop(dialogContext);
                ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Denuncia enviada para moderacao.')));
              }
            } catch (e) {
              if (dialogContext.mounted) {
                ScaffoldMessenger.of(dialogContext).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
              }
            }
          },
          child: const Text('Enviar denuncia'),
        ),
      ],
    ),
  );
}
