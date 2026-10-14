import 'package:shared_preferences/shared_preferences.dart';

/// Token em memoria (lido pelo interceptor do Dio a cada requisicao) +
/// persistencia em disco (SharedPreferences) para sobreviver a um F5.
class AuthSession {
  static String? token;
  static String? usuarioId;
  static String? papel;

  static bool get autenticado => token != null;

  static Future<void> salvar() async {
    if (token == null) return;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('qjrpg_token', token!);
  }

  static Future<void> carregar() async {
    final prefs = await SharedPreferences.getInstance();
    token = prefs.getString('qjrpg_token');
  }

  static Future<void> limpar() async {
    token = null;
    usuarioId = null;
    papel = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('qjrpg_token');
  }
}
