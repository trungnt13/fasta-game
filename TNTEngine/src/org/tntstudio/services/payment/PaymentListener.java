
package org.tntstudio.services.payment;

import org.tntstudio.services.payment.PaymentCallback.PaymentType;

/** This interface to handle payment event from activity
 * 
 * @FileName: IActivityHandler.java
 * @CreateOn: Sep 16, 2012 - 3:42:14 PM
 * @Author: TrungNT */
public interface PaymentListener {
	/** @param type {@link PaymentType}
	 * @param value the value of purchase */
	public void onPaymentSuccess (PaymentType type, int value);

	/** @param type type {@link PaymentType}
	 * @param value the value of purchase */
	public void onPaymentFailure (PaymentType type, int value);

	/** @param type type {@link PaymentType}
	 * @param value the value of purchase */
	public void onPaymentDeny (PaymentType type, int value);
}
