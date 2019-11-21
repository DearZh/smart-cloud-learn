package com.smart.cloud.smartplatformcommon.sftp.pool;

public class PoolException extends Exception{

	private static final long serialVersionUID = 7420550356674087347L;

	public PoolException() {
		super("pool exception");
	}
	
	public PoolException(String msg) {
        super(msg);
    }

    public PoolException(String msg, Throwable t) {
        super(msg, t);
    }
    
}


