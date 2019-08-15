package com.linor.singer.config;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

public class AtomikosPlatform implements JtaPlatform {
	private static final long serialVersionUID = 1L;
	private final TransactionManager txMgr;

	private final UserTransaction userTx;

	public AtomikosPlatform() {
		super();
		this.txMgr = new UserTransactionManager();
		this.userTx = new UserTransactionImp();
	}

	@Override
	public TransactionManager retrieveTransactionManager() {
		return this.txMgr;
	}

	@Override
	public UserTransaction retrieveUserTransaction() {
		return this.userTx;
	}

	@Override
	public Object getTransactionIdentifier(Transaction transaction) {
		return transaction;
	}

	@Override
	public boolean canRegisterSynchronization() {
		try {
			if (this.txMgr.getTransaction() != null) {
				return this.txMgr.getTransaction().getStatus() == Status.STATUS_ACTIVE;
			}
		} catch (SystemException se) {
			throw new TransactionException("Could not determine transaction status", se);
		}
		return false;
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) {
		try {
			this.txMgr.getTransaction().registerSynchronization(synchronization);
		} catch (Exception e) {
			throw new JtaPlatformException("Could not access JTA Transaction to register synchronization", e);
		}
	}

	@Override
	public int getCurrentStatus() throws SystemException {
		return retrieveTransactionManager().getStatus();
	}

}
