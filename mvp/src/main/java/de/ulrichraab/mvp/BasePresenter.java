/*
 * Copyright (C) 2015 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ulrichraab.mvp;


import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Base {@link Presenter} implementation that implements the <a href="https://en.wikipedia.org/wiki/Null_Object_pattern">
 * null object pattern</a> for the attached UI. Whenever there is no attached UI, a null object UI will be set that logs
 * method calls on the UI. This avoids {@link NullPointerException NullPointerExceptions} and checks like {@code if
 * (ui() != null)}.
 * @author Ulrich Raab
 */
public abstract class BasePresenter<T extends Ui> implements Presenter<T> {

   private static final Logger LOG = Logger.getLogger(BasePresenter.class.getName());

   private WeakReference<T> uiReference;
   private final T uiProxy;

   /**
    * Creates a new {@link BasePresenter} instance.
    */
   public BasePresenter () {
      try {
         Class<?> clazz = getClass();
         Class<T> uiClass = null;
         while (uiClass == null) {
            Type genericSuperclassType = clazz.getGenericSuperclass();
            while (!(genericSuperclassType instanceof ParameterizedType)) {
               clazz = clazz.getSuperclass();
               genericSuperclassType = clazz.getGenericSuperclass();
            }
            Type[] typeArgs = ((ParameterizedType) genericSuperclassType).getActualTypeArguments();
            for (Type typeArg : typeArgs) {
               Class<?> typeArgClass = (Class<?>) typeArg;
               if (typeArgClass.isInterface() && isUiSubtype(typeArgClass)) {
                  // noinspection unchecked
                  uiClass = (Class<T>) typeArgClass;
                  break;
               }
            }
            clazz = clazz.getSuperclass();
         }
         uiProxy = createUiProxy(uiClass);
      }
      catch (Throwable t) {
         String msg = "The generic type <T extends Ui> must be the first generic type argument of this class (per " +
                      "convention). Otherwise the type of Ui which this Presenter coordinates can't be determined.";
         throw new RuntimeException(msg, t);
      }
   }

   @Override
   public void onAttachUi (T ui) {
      LOG.info("onAttachUi(" + ui.getClass().getName() + ")");
      uiReference = new WeakReference<>(ui);
   }

   @Override
   public void onDetachUi () {
      LOG.info("onDetachUi()");
      if (uiReference != null) {
         uiReference.clear();
      }
      uiReference = null;
   }

   /**
    * Returns the attached user interface managed by this presenter.
    * @return The UI managed by this presenter.
    */
   public T ui () {
      return (uiReference == null) ? uiProxy : uiReference.get();
   }

   /**
    * Scans the interface inheritance hierarchy and checks if the root is {@link Ui}.
    * @param clazz The leaf where to begin to scan.
    * @return {@code true} if leaf is a subtype of {@link Ui}, {@code false} otherwise.
    */
   private boolean isUiSubtype (Class<?> clazz) {
      if (clazz.equals(Ui.class)) {
         return true;
      }
      for (Class interfaceClass : clazz.getInterfaces()) {
         if (isUiSubtype(interfaceClass)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns an instance of the dynamically built class for the specified UI interface. Method invocations on the
    * returned instance are forwarded to a {@link LoggingInvocationHandler}.
    * @param uiClass UI interface that will be implemented by the returned proxy object.
    * @param <T> UI interface type.
    * @return A new proxy object that delegates method calls to a {@link LoggingInvocationHandler}.
    */
   private static <T> T createUiProxy (Class<T> uiClass) {
      // noinspection unchecked
      return (T) Proxy.newProxyInstance(
            uiClass.getClassLoader(),
            new Class[] {uiClass},
            new LoggingInvocationHandler()
      );
   }

   /**
    * Invocation handler that logs method invocations.
    */
   private static class LoggingInvocationHandler implements InvocationHandler {

      private static final String TAG = LoggingInvocationHandler.class.getName();
      private static final Map<Class<?>, Object> DEFAULT_VALUES = Collections.unmodifiableMap(
            new HashMap<Class<?>, Object>() {{
               put(Boolean.TYPE, false);
               put(Byte.TYPE, (byte) 0);
               put(Character.TYPE, '\000');
               put(Double.TYPE, 0.0d);
               put(Float.TYPE, 0.0f);
               put(Integer.TYPE, 0);
               put(Long.TYPE, 0L);
               put(Short.TYPE, (short) 0);
            }}
      );

      @Override
      public Object invoke (Object proxy, Method method, Object[] args) throws Throwable {
         // Log the method invocation
         StringBuilder sb = new StringBuilder();
         sb.append(method.getName()).append("(");
         for (int i = 0; args != null && i < args.length; i++) {
            if (i != 0) {
               sb.append(", ");
            }
            sb.append(args[0]);
         }
         sb.append(")");
         LOG.info(sb.toString());
         return DEFAULT_VALUES.get(method.getReturnType());
      }
   }
}
