import 'package:frontend/core/api/api_client.dart';
import 'package:frontend/features/auth/data/models/user_model.dart';

class AuthRemoteDataSource {
  ApiClient apiClient;
  AuthRemoteDataSource({required this.apiClient});

  Future<UserModel> login({required String email, required String password}) async {
    final response = await apiClient.post('/auth/login', {
      'email': email,
      'password': password,
    }, (json) => UserModel.fromJson(json));
    if (!response.success) throw response.error!;
    return response.result!;
  }

  Future<UserModel> register({required String username, required String email, required String password}) async {
    final response = await apiClient.post('/auth/register', {
      'username': username,
      'email': email,
      'password': password,
    }, (json) => UserModel.fromJson(json));
    if (!response.success) throw response.error!;
    return response.result!;
  }
}
