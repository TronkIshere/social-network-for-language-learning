import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:frontend/core/api/api_response.dart';
import 'package:http/http.dart' as http;

class ApiClient {
  final String baseUrl;
  final _storage = const FlutterSecureStorage();

  ApiClient({required this.baseUrl});

  Future<Map<String, String>> _getHeaders() async {
    final token = await _storage.read(key: 'token');
    return {
      'Content-Type': 'application/json',
      if (token != null && token.isNotEmpty) 'Authorization': 'Bearer $token',
    };
  }

  Future<ApiResponse<T>> get<T>(String path, T Function(dynamic) fromJsonT) async {
    final headers = await _getHeaders();
    final response = await http.get(Uri.parse('$baseUrl$path'), headers: headers);
    return _handleResponse(response, fromJsonT);
  }

  Future<ApiResponse<T>> post<T>(String path, Map<String, dynamic> body, T Function(dynamic) fromJsonT) async {
    final headers = await _getHeaders();
    final response = await http.post(Uri.parse('$baseUrl$path'), headers: headers, body: jsonEncode(body));
    return _handleResponse(response, fromJsonT);
  }

  ApiResponse<T> _handleResponse<T>(http.Response response, T Function(dynamic) fromJsonT) {
    final Map<String, dynamic> json = jsonDecode(response.body);

    if (json['success'] == true) {
      return ApiResponse<T>.fromJson(json, fromJsonT);
    } else {
      return ApiResponse<T>.fromJson(json, fromJsonT);
    }
  }
}
