package com.damnhandy.uri.template;

import static java.util.Objects.requireNonNull;

/**
 * Created by nfischer on 8/14/2016.
 */
public class Either<Left, Right> {
    public final Left left;
    public final Right right;

    private Either(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public static <Left, Right> Either<Left, Right> left(Left left){
        requireNonNull(left);
        return new Either<>(left, null);
    }

    public static <Left, Right> Either<Left, Right> right(Right right){
        requireNonNull(right);
        return new Either<>(null, right);
    }

    public Object get(){
        if(left == null)
            return right;
        else return left;
    }

    public boolean isLeft(){
        return left != null;
    }

    public boolean isRight(){
        return !isLeft();
    }

    @Override
    public String toString() {
        return "Either{" +
        "left=" + left +
        ", right=" + right +
        '}';
    }
}
