package org.solmix.runtime.transaction.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.transaction.config.TransactionMeta;

public class AnnotationTransactionMetaCreator extends
		AbstractTransactionMetaCreator {
	private final boolean publicMethodsOnly;
	private final Set<TransactionAnnotationParser> annotationParsers;
	public AnnotationTransactionMetaCreator() {
		this(true);
	}
	
	public AnnotationTransactionMetaCreator(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		this.annotationParsers = new LinkedHashSet<TransactionAnnotationParser>(1);
		this.annotationParsers.add(new TxAnnotationParser());
		
	}
	
	public AnnotationTransactionMetaCreator(TransactionAnnotationParser annotationParser) {
		this.publicMethodsOnly = true;
		Assert.isNotNull(annotationParser, "TransactionAnnotationParser must not be null");
		this.annotationParsers = Collections.singleton(annotationParser);
	}

	/**
	 * Create a custom AnnotationTransactionAttributeSource.
	 * @param annotationParsers the TransactionAnnotationParsers to use
	 */
	public AnnotationTransactionMetaCreator(TransactionAnnotationParser... annotationParsers) {
		this.publicMethodsOnly = true;
		Set<TransactionAnnotationParser> parsers = new LinkedHashSet<TransactionAnnotationParser>(annotationParsers.length);
		Collections.addAll(parsers, annotationParsers);
		this.annotationParsers = parsers;
	}
	@Override
	protected TransactionMeta findTransactionAttribute(Method method) {
		return determineTransactionAttribute(method);
	}

	@Override
	protected TransactionMeta findTransactionAttribute(Class<?> clazz) {
		return determineTransactionAttribute(clazz);
	}
	protected TransactionMeta determineTransactionAttribute(AnnotatedElement ae) {
		for (TransactionAnnotationParser annotationParser : this.annotationParsers) {
			TransactionMeta attr = annotationParser.parseTransactionAnnotation(ae);
			if (attr != null) {
				return attr;
			}
		}
		return null;
	}
	@Override
	protected boolean allowPublicMethodsOnly() {
		return this.publicMethodsOnly;
	}
}
