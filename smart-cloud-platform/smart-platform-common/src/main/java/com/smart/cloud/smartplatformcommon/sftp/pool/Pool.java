package com.smart.cloud.smartplatformcommon.sftp.pool;

import org.apache.commons.pool.BaseObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public abstract class Pool<T> {

    private final BaseObjectPool internalPool;

    public Pool(GenericObjectPool.Config poolConfig, PoolableObjectFactory factory) {
        //TODO arnold.zhao 2019/4/5

        this.internalPool = new GenericObjectPool(factory, poolConfig);
    }

    public T borrowResource() throws Exception {
        try {
            return (T) this.internalPool.borrowObject();
        } catch (Exception e) {
            throw new Exception("get resource failed!!!");
        }
    }

    public void returnResource(T resource) throws Exception {
        try {
            /*
                FIXME  此处并未实际挂起链接对象，只是GenericObjectPool中实际链接池数量的_numActive减一,_pool的空闲对象集合 +1 obj；
                在对应的SftpPoolableObjectFactory中重写对应的passivateObject方法，已达到真正的资源回收的效果；
                arnold.zhao 2019/4/7
             */
            this.internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new Exception("return resource failed!!!");
        }
    }

    public void destroy() throws Exception {
        try {
            this.internalPool.close();
        } catch (Exception e) {
            throw new Exception("pool destroy failed!!!");
        }
    }
}
