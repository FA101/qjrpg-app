//import 'features/tag/presentation/tag_list_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'features/evento/presentation/evento_list_page.dart';

void main() {
  runApp(const ProviderScope(child: QjrpgApp()));
}

class QjrpgApp extends StatelessWidget {
  const QjrpgApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Quero Jogar RPG',
      theme: ThemeData(colorSchemeSeed: Colors.deepPurple, useMaterial3: true),
      home: const EventoListPage(),
    );
  }
}
