package jnr.ffi.mapper;


import jnr.ffi.*;
import jnr.ffi.Runtime;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.LongLong;
import jnr.ffi.annotations.Out;
import jnr.ffi.provider.converters.EnumSetConverter;
import jnr.ffi.types.size_t;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class AnnotatedMappedTypeTest {
    public static final class CustomPointer {
        private final Pointer pointer;

        public CustomPointer(Pointer pointer) {
            this.pointer = pointer;
        }

        @ToNativeConverter.ToNative(nativeType = jnr.ffi.Pointer.class)
        public static Pointer toNative(CustomPointer value, ToNativeContext context) {
            return value != null ? value.pointer : null;
        }

        @FromNativeConverter.FromNative(nativeType = jnr.ffi.Pointer.class)
        public static CustomPointer fromNative(Pointer value, FromNativeContext context) {
            return value != null ? new CustomPointer(value) : null;
        }
    }
    
    public static interface TestLib {
        CustomPointer ptr_malloc(@size_t int size);
        void ptr_free(CustomPointer ptr);
    }

    static TestLib testlib;
    static Runtime runtime;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        testlib = TstUtil.loadTestLib(TestLib.class);
        runtime = Runtime.getRuntime(testlib);
    }
    
    @Test public void returnsInstanceOfCorrectClass() {
        assertSame(CustomPointer.class, testlib.ptr_malloc(1).getClass());
    }
    
    @Test public void toNative() {
        testlib.ptr_free(testlib.ptr_malloc(1));
    }
}
