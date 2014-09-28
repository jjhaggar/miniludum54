package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ConfigControllers {
	// Botones del mando / Gamepad Buttons
	private BaseScreen screen;
	public ControllerListener controllerListener;
	public boolean leftPressed = false;
	public boolean rightPressed = false;
	public boolean jumpPressed = false;
	public boolean shootPressed = false;

	public boolean activateJump = false;

	public boolean leftPressed2P, rightPressed2P, jumpPressed2P, shootPressed2P = false;

    public ConfigControllers(MainScreen screen) {
    	this.screen = screen;
    }

    /*
    public ConfigControllers(MenuScreen screen) {
        this.screen = screen;
    }

    public ConfigControllers(GameOverScreen screen) {
        this.screen = screen;
    }

    public ConfigControllers(IntroScreen screen) {
        this.screen = screen;
    }

    public ConfigControllers(EndingScreen screen) {
        this.screen = screen;
    }

    public ConfigControllers(CreditsScreen screen) {
        this.screen = screen;
    }
    */

    public void init() {

        // CODIGO DE PRUEBAS PARA LOS MANDOS / GAMEPAD TESTING CODE

        // print the currently connected controllers to the console
        System.out.println("Controllers: " + Controllers.getControllers().size);
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            System.out.println("#" + i++ + ": " + controller.getName());
        }
        if (Controllers.getControllers().size == 0)
            System.out.println("No controllers attached");

        // setup the listener that prints events to the console
        controllerListener = new ControllerListener(){
                public int indexOf(Controller controller) {
                    return Controllers.getControllers().indexOf(controller, true);
                }

                @Override
                public void connected (Controller controller) {
                    System.out.println("connected " + controller.getName());
                    int i = 0;
                    for (Controller c : Controllers.getControllers()) {
                        System.out.println("#" + i++ + ": " + c.getName());
                    }
                }

                @Override
                public void disconnected (Controller controller) {
                    System.out.println("disconnected " + controller.getName());
                    int i = 0;
                    for (Controller c : Controllers.getControllers()) {
                        System.out.println("#" + i++ + ": " + c.getName());
                    }
                    if (Controllers.getControllers().size == 0) System.out.println("No controllers attached");
                }

                @Override
                public boolean buttonDown (Controller controller, int buttonIndex) {
                    if (screen.getClass().equals(MainScreen.class)){
                    	if (indexOf(controller) == 0){
		                    if (buttonIndex == 0  && !ConfigControllers.this.jumpPressed){
		                    	activateJump = true;
		                    	ConfigControllers.this.jumpPressed = true;
		                    }
                    	}
                    	if (indexOf(controller) == 1){
		                    if (buttonIndex == 0  && !ConfigControllers.this.jumpPressed2P){
		                        ((MainScreen) ConfigControllers.this.screen).jumpBoss();
		                        ConfigControllers.this.jumpPressed2P = true;
		                    }
                    	}
                    /*
                    if ((buttonIndex == 1 || buttonIndex == 2) && !ConfigControllers.this.shootPressed){
                        ((MainScreen) ConfigControllers.this.screen).shoot();
                        ConfigControllers.this.shootPressed = true;
                    }
                    */
                }
                	/*
                else if (screen.getClass().equals(MenuScreen.class)){
                    if (buttonIndex == 0){
                        ((MenuScreen) ConfigControllers.this.screen).enterButtonPressed();
                    }
                }
                else if (screen.getClass().equals(GameOverScreen.class)){
                    if (buttonIndex == 0){
                        ((GameOverScreen) ConfigControllers.this.screen).enterButtonPressed();
                    }
                }
                else if (screen.getClass().equals(IntroScreen.class)){
                    if (buttonIndex == 0){
                        ((IntroScreen) ConfigControllers.this.screen).enterButtonPressed();
                    }
                }
                else if (screen.getClass().equals(EndingScreen.class)){
                    if (buttonIndex == 0){
                        ((EndingScreen) ConfigControllers.this.screen).enterButtonPressed();
                    }
                }
                else if (screen.getClass().equals(CreditsScreen.class)){
                    if (buttonIndex == 0){
                        ((CreditsScreen) ConfigControllers.this.screen).enterButtonPressed();
                    }
                }

                */

                return false;
            }

                @Override
                public boolean buttonUp (Controller controller, int buttonIndex) {
                    // System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " up");
                	if (indexOf(controller) == 0){
	                    if (buttonIndex == 0){
	                        ConfigControllers.this.jumpPressed = false;
	                    }
	                    if ((buttonIndex == 1 || buttonIndex == 2)){
	                        ConfigControllers.this.shootPressed = false;
	                    }
                	}
                	if (indexOf(controller) == 1){
	                    if (buttonIndex == 0){
	                        ConfigControllers.this.jumpPressed2P = false;
	                    }
	                    if ((buttonIndex == 1 || buttonIndex == 2)){
	                        ConfigControllers.this.shootPressed2P = false;
	                    }
                	}
                    return false;
                }

                @Override
                public boolean axisMoved (Controller controller, int axisIndex, float value) {
                    // System.out.println("#" + indexOf(controller) + ", axis " + axisIndex + ": " + value);
                    return false;
                }

                @Override
                public boolean xSliderMoved (Controller controller, int sliderIndex, boolean value) {
                    // System.out.println("#" + indexOf(controller) + ", x slider " + sliderIndex + ": " + value);
                    return false;
                }

                @Override
                public boolean ySliderMoved (Controller controller, int sliderIndex, boolean value) {
                    // System.out.println("#" + indexOf(controller) + ", y slider " + sliderIndex + ": " + value);
                    return false;
                }

                @Override
                public boolean accelerometerMoved (Controller controller, int accelerometerIndex, Vector3 value) {
                    // not printing this as we get to many values
                    return false;
                }

                @Override
                public boolean povMoved(Controller controller, int povCode, PovDirection value) {


                	System.out.println("IndexOf = "+ indexOf(controller));


                	if (indexOf(controller) == 0){
	                    if (value.equals("west") || value == PovDirection.west){
	                        rightPressed = false;
	                        leftPressed = true;
	                        ((MainScreen) ConfigControllers.this.screen).activateLeftRunning();
	                    }
	                    else if (value.equals(PovDirection.east)){
	                        rightPressed = true;
	                        leftPressed = false;
	                        ((MainScreen) ConfigControllers.this.screen).activateRightRunning();
	                    }
	                    else if (value.equals(PovDirection.center)){
	                        rightPressed = false;
	                        leftPressed = false;
	                    }
                	}
                	if (indexOf(controller) == 1){
	                    if (value.equals("west") || value == PovDirection.west){
	                        rightPressed2P = false;
	                        leftPressed2P = true;
	                        ((MainScreen) ConfigControllers.this.screen).activateLeftRunningBoss();
	                    }
	                    else if (value.equals(PovDirection.east)){
	                        rightPressed2P = true;
	                        leftPressed2P = false;
	                        ((MainScreen) ConfigControllers.this.screen).activateRightRunningBoss();
	                    }
	                    else if (value.equals(PovDirection.center)){
	                        rightPressed2P = false;
	                        leftPressed2P = false;
	                    }
                	}


                    // else System.out.println("else!!");
                    return false;
                }
            };

        Controllers.addListener(controllerListener);
    }

    public void terminate(){
        Controllers.removeListener(controllerListener);
    }
}