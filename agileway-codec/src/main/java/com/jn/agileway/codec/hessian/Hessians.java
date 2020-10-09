package com.jn.agileway.codec.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.factory.Factory;
import com.jn.langx.factory.ThreadLocalFactory;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessians {
    private Hessians() {
    }

    public static final ThreadLocalFactory<?, Hessian2Output> hessian2OutputFactory = new ThreadLocalFactory<Object, Hessian2Output>(new Factory<Object, Hessian2Output>() {
        @Override
        public Hessian2Output get(Object o) {
            return new Hessian2Output();
        }
    });

    public static final ThreadLocalFactory<?, Hessian2Input> hessian2InputFactory = new ThreadLocalFactory<Object, Hessian2Input>(new Factory<Object, Hessian2Input>() {
        @Override
        public Hessian2Input get(Object o) {
            return new Hessian2Input();
        }
    });

    public static <T> byte[] serialize(T o) throws IOException {
        return serialize(hessian2OutputFactory, o);
    }

    public static <T> byte[] serialize(Factory<?, Hessian2Output> hessian2OutputFactory, T o) throws IOException {
        if (o == null) {
            return null;
        }

        Hessian2Output output = null;
        try {
            output = hessian2OutputFactory.get(null);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            output.init(bao);
            output.writeObject(o);
            output.flush();
            return bao.toByteArray();
        } finally {
            if (hessian2OutputFactory instanceof ThreadLocalFactory) {
                if (output != null) {
                    output.reset();
                }
            } else {
                IOs.close(output);
            }
        }
    }

    public static <T> T deserialize(byte[] bytes) throws IOException {
        return deserialize(hessian2InputFactory, bytes);
    }


    public static <T> T deserialize(Factory<?, Hessian2Input> hessian2InputFactory, byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Hessian2Input input = null;
        try {
            input = hessian2InputFactory.get(null);
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input.init(bai);
            return (T) input.readObject();
        } finally {
            if (hessian2InputFactory instanceof ThreadLocalFactory) {
                if (input != null) {
                    input.reset();
                }
            } else {
                IOs.close(input);
            }
        }
    }

    public static <T> T deserialize(byte[] bytes, @NonNull Class targetType) throws IOException {
        return deserialize(hessian2InputFactory, bytes, targetType);
    }


    public static <T> T deserialize(Factory<?, Hessian2Input> hessian2InputFactory, byte[] bytes, @NonNull Class targetType) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Preconditions.checkNotNull(targetType, "target type is null");
        Hessian2Input input = null;
        try {
            input = hessian2InputFactory.get(null);
            ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
            input.init(bai);
            return (T) input.readObject(targetType);
        } finally {
            if (hessian2InputFactory instanceof ThreadLocalFactory) {
                if (input != null) {
                    input.reset();
                }
            } else {
                IOs.close(input);
            }
        }
    }

}
