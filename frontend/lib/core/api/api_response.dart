class ApiResponse<T> {
  final bool success;
  final T? result;
  final ApiError? error;

  ApiResponse({required this.success, this.result, this.error});

  factory ApiResponse.fromJson(Map<String, dynamic> json, T Function(dynamic) fromJsonT) {
    return ApiResponse<T>(
      success: json['success'] ?? false,
      result: json['result'] != null ? fromJsonT(json['result']) : null,
      error: json['error'] != null ? ApiError.fromJson(json['error']) : null,
    );
  }
}

class ApiError {
  final int code;
  final String message;

  ApiError({required this.code, required this.message});

  factory ApiError.fromJson(Map<String, dynamic> json) {
    return ApiError(
      code: json['code'] ?? json['status'] ?? -1,
      message: json['message'] ?? json['error'] ?? 'Unknown error',
    );
  }
}
