package com.example;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;

import java.util.Map;

public class InjectorFetcher {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ParameterSupplier.class);
    private final ExtensionContext extensionContext;

    InjectorFetcher(final ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    boolean supports(final ParameterContext parameterContext) {
        final Map<Key<?>, Binding<?>> bindings = getBindings();
        final Key<?> key = createKey(parameterContext);
        return bindings.containsKey(key);
    }

    private Key<?> createKey(final ParameterContext parameterContext) {
        return Key.get(parameterContext.getParameter().getType());
    }

    private Map<Key<?>, Binding<?>> getBindings() {
        final Injector injector = fetchInjector();
        return injector.getAllBindings();
    }

    private Injector fetchInjector() {
        final ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);
        return store.getOrComputeIfAbsent(Injector.class, i -> Guice.createInjector(new Module()), Injector.class);
    }

    public Object getObject(final ParameterContext parameterContext) {
        return fetchInjector().getInstance(createKey(parameterContext));
    }
}