package com.cloudyna.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Logger {
    
    private LambdaLogger lambdaLogger = null;
    
    public Logger(Context context) {
        if (context != null) {
            this.lambdaLogger = context.getLogger();            
        }
    }
    
    public void log(String msg) {
        if (lambdaLogger != null)  {
            lambdaLogger.log(msg);
        } else {
            System.out.println(msg);                
        }
    }
}
