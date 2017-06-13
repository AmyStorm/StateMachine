package com.zzy.dsl.grammar;

import java.util.LinkedList;

/**
 * Created by ZhengZiyu on 2017/3/25.
 */
public class TokenContainer {
    private LinkedList<Token> tokenLinkedList = new LinkedList<Token>();
    private Integer currentIndex = 0;
    private Token currentToken;
    public TokenContainer(LinkedList<Token> list){
        this.tokenLinkedList = list;
        if(tokenLinkedList != null && tokenLinkedList.size() > 0){
            currentToken = tokenLinkedList.get(0);
        }else{
            throw new RuntimeException("tokenList cannot be empty");
        }
    }
    public void addToken(Token token){
        tokenLinkedList.add(token);
    }

    public boolean popToken(){
        if(!isEnd()){
            currentIndex ++;
            currentToken = tokenLinkedList.get(currentIndex);
        }
        return true;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public boolean isEnd(){
        return tokenLinkedList.size() == currentIndex + 1;
    }

    public void resetToIndex(Integer index){
        currentIndex = index;
        currentToken = tokenLinkedList.get(index);
    }
}
