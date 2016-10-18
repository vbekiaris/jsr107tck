/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

/**
 This package contains infrastructure so that expiry policy can send
 information back to the JUnit test which initiated them so that asserts can
 happen.

 An instance of {@link org.jsr107.tck.expiry.ExpiryPolicyServer} is created in the JUnit test,
 listening on port 10,005.

 This way no assumption is made about whether a expiry policy is running
 in-process or out of process.

 @author Greg Luck
 @author Brian Oliver
 @author Joe Fialli
 */
package org.jsr107.tck.expiry;
