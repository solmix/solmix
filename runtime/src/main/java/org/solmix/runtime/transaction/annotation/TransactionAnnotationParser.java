package org.solmix.runtime.transaction.annotation;

import java.lang.reflect.AnnotatedElement;

import org.solmix.runtime.transaction.config.TransactionMeta;

public interface TransactionAnnotationParser {
	TransactionMeta parseTransactionAnnotation(AnnotatedElement ae);
}
