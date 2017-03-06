package org.solmix.runtime.transaction.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;

import org.solmix.runtime.transaction.config.TransactionMeta;
import org.solmix.runtime.transaction.proxy.NotRollbackRule;
import org.solmix.runtime.transaction.proxy.RollbackRule;
import org.solmix.runtime.transaction.proxy.RuledTransactionMeta;

public class TxAnnotationParser implements TransactionAnnotationParser {

	@Override
	public TransactionMeta parseTransactionAnnotation(AnnotatedElement ae) {
		
		Transaction tx=ae.getAnnotation(Transaction.class);
		if(tx==null){
			return null;
		}
		RuledTransactionMeta meta = new RuledTransactionMeta();
		meta.setTransactionPolicy(tx.policy());
		meta.setTransactionIsolation(tx.isolation());
		meta.setTimeout(tx.timeout());
		meta.setReadOnly(tx.readOnly());
		meta.setQualifier(tx.value());
		
		ArrayList<RollbackRule> rollBackRules = new ArrayList<RollbackRule>();
		Class<?>[] rbf = tx.rollbackFor();
		for (Class<?> rbRule : rbf) {
			RollbackRule rule = new RollbackRule(rbRule);
			rollBackRules.add(rule);
		}
		String[] rbfc = tx.rollbackForClassName();
		for (String rbRule : rbfc) {
			RollbackRule rule = new RollbackRule(rbRule);
			rollBackRules.add(rule);
		}
		Class<?>[] nrbf = tx.noRollbackFor();
		for (Class<?> rbRule : nrbf) {
			NotRollbackRule rule = new NotRollbackRule(rbRule);
			rollBackRules.add(rule);
		}
		String[] nrbfc = tx.noRollbackForClassName();
		for (String rbRule : nrbfc) {
			NotRollbackRule rule = new NotRollbackRule(rbRule);
			rollBackRules.add(rule);
		}
		meta.getRollbackRules().addAll(rollBackRules);
		return meta;
	}

}
