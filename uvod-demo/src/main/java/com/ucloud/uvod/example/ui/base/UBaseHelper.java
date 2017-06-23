package com.ucloud.uvod.example.ui.base;

import android.content.Context;

/**
 * 
 * Created by lw.tan on 2015/10/10.
 *
 */
public abstract class UBaseHelper {
    public interface ChangeListener {
    	void onUpdateUI();
    }
    //当前进度
    protected int currentLevel;
	//最大进度
    protected int maxLevel;
	//历史进度
    protected int historyLevel;
	//每次增加的粒度
    protected int levelStep;
    protected ChangeListener changeListener;
    protected Context context;
    public abstract void init(Context context);
    public abstract void setValue(int level, boolean isTouch);
    public abstract int getSystemValueLevel();

    public UBaseHelper(Context context) {
    	this.context = context;
    	init(context);
    }

    public int getCurrentLevel() {
    	return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
    	this.currentLevel = currentLevel;
    }

    public int getMaxLevel() {
    	return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
    	this.maxLevel = maxLevel;
    }
	
    public int getHistoryLevel() {
    	return historyLevel;
    }
	
    public void setHistoryLevel(int historyLevel) {
    	this.historyLevel = historyLevel;
    }
	
    public int getLevel() {
    	return levelStep;
    }
	
    public void setLevel(int level) {
    	levelStep = level;
    }
	
    public ChangeListener getChanageListener() {
    	return changeListener;
    }
	
    public void setOnChangeListener(ChangeListener l) {
    	changeListener = l;
    }
	
    public void increaseValue() {
    	setValue(currentLevel + levelStep, false);
    }
	
    public void decreaseValue() {
    	setValue(currentLevel - levelStep, false);
    }
	
    public boolean isZero() {
    	return currentLevel == 0;
    }
	
    public void setToZero() {
    	setValue(0, false);
    }
	
    public void updateValue() {
    	currentLevel = getSystemValueLevel();
    }
	
    public void setVauleTouch(int level) {
    	setValue(level, true);
    }
}
