import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'tag_providers.dart';

class TagListPage extends ConsumerWidget {
  const TagListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final tagsAsync = ref.watch(tagsProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Tags')),
      body: tagsAsync.when(
        data: (tags) => ListView(
          children: tags
              .map((t) => ListTile(
                    leading: CircleAvatar(backgroundColor: Color(int.parse(t.corHex.replaceFirst('#', '0xFF')))),
                    title: Text(t.nome),
                    subtitle: Text(t.tipo.name),
                  ))
              .toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
