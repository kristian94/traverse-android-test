package com.cborum.traverse.backend;

/**
 * Created by ChristopherBorum on 21/11/2016.
 */

public abstract class TraverseCallback implements ITraverseCallback {

    public abstract void onSuccess(int success);

    public abstract void onError(int error);
}
