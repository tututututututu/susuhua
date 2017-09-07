package com.tutu.susuhua.rxbus;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * Created by 47066 on 2017/9/7.
 */


public class RxBus2 {
    //相当于Rxjava1.x中的Subject
    private final FlowableProcessor<Object> mBus;
    private static volatile RxBus2 sRxBus = null;

    private RxBus2() {
        //调用toSerialized()方法，保证线程安全
        mBus = PublishProcessor.create().toSerialized();
    }

    public static synchronized RxBus2 getDefault() {
        if (sRxBus == null) {
            synchronized (RxBus2.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus2();
                }
            }
        }
        return sRxBus;
    }


    /**
     * 发送消息
     *
     * @param o
     */
    public void post(Object o) {
        new SerializedSubscriber<>(mBus).onNext(o);
    }

    /**
     * 确定接收消息的类型
     *
     * @param aClass
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toFlowable(Class<T> aClass) {
        return mBus.ofType(aClass);
    }

    /**
     * 判断是否有订阅者
     *
     * @return
     */
    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

}


