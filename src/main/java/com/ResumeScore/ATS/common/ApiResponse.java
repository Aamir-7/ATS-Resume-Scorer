package com.ResumeScore.ATS.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    ApiResponse(){
    }

    public ApiResponse(boolean success,String message,T data){
        this.success=success;
        this.data=data;
        this.message=message;
    }

    public static<T> ApiResponse<T> success(String message,T data){
        return new ApiResponse<>(true,message,data);
    }

    public static<T> ApiResponse<T> error(String message,T data){
        return new ApiResponse<>(false,message,data);
    }


}
