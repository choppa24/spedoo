package com.example.android.speedo;

//класс для хранения и вычисления параметров движения на интервале

public class MeasurePoint {
    private float x;
    private float y;
    private float z;
    private float speedBefore;
    private float speedAfter;
    private float distance;
    private float acceleration;
    private long interval;


    public MeasurePoint(float x, float y, float z, float speedBefore, long interval, float distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.speedBefore = speedBefore;
        this.interval = interval;
        this.distance = distance;
        speedAfter = 0;
        calc();
    }

    private void calc(){
        //Acceleration as projection of current vector on average
        System.out.println(this.x + "   "+ this.y+ "    "+this.z);
        acceleration = (float) Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
        float t = ((float)interval / 1000f);
        speedAfter = speedBefore + acceleration * t; // v=v0+at;
        System.out.println("Скорость до: "+ speedBefore + "   Ускорение: " + acceleration + "    Время: " + t + "   Скорость: " + speedAfter);
        distance += speedBefore*t + acceleration*t*t/2;// s=v0*t + a*t^2/2
    }
// add getters
    public float getSpeedAfter(){

        return speedAfter;
    }
    public float getDistance(){
        return  distance;
    }
    public float getSpeedBefore(){ return speedBefore;}
}
