import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/widgets/app_drawer.dart';
import 'link_util_providers.dart';

class LinkUtilListPage extends ConsumerWidget {
  const LinkUtilListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final linksAsync = ref.watch(linksUteisProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Links uteis')),
      drawer: const AppDrawer(),
      body: linksAsync.when(
        data: (links) => ListView(
          children: links.map((l) => ListTile(title: Text(l.titulo), subtitle: Text(l.categoria))).toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
