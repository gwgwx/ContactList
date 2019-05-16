/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import online.touch.easy.common.model.Account;



/**
 *
 * @author Georgios
 */
public class AccountRepository extends AbstractRepository<Account> {
    
    private static AccountRepository instance = null;

    private AccountRepository() {
        super(Account.class, "accounts");
    }

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }


}
