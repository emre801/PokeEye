package com.johnerdo.battle;

import java.util.Iterator;
import java.util.LinkedList;

import org.johnerdo.globalInfo.PokemonList;

import com.johnerdo.imageCompare.MatchingMethod;
import com.johnerdo.imageCompare.RobotBot;
import com.pokejava.Pokemon;

public class Battle {

	//LinkedList<String> pokemonOnScreen;
	LinkedList<Pokemon> pokemon;
	public Battle(){
		this.pokemon = new LinkedList<Pokemon>();
		PokemonList.setMapping();
	}
	public void getPokemonOnScreen(boolean pushButton) throws InterruptedException{
		if(pushButton){
			RobotBot.Screen();
			Thread.sleep(1500);
		}
		setUp();
		printPokemon();
	}
	public static void getHealth(boolean pushButton) throws InterruptedException{
		if(pushButton){
			RobotBot.Screen();
			Thread.sleep(1500);
		}
		LinkedList<Double> health = MatchingMethod.getHealthBars();
		for(Double d: health){
			System.out.println(d);
		}
	}
	public void setUp(){
		LinkedList<Integer> pokemonNums = MatchingMethod.getPokemonNumbersOnScreen();
		for(Integer dexNum :pokemonNums){
			if(dexNum<720)
				pokemon.add(new Pokemon(dexNum));
			else
				pokemon.add(new Pokemon(PokemonList.pokemonNames[dexNum]));
		}
		MatchingMethod.copyGifsName(pokemonNums);
	}
	public void printPokemon(){
		Iterator<Pokemon> pokIter = pokemon.iterator();
		while(pokIter.hasNext()){
			Pokemon pok1 = pokIter.next();
			//Pokemon pok2 = pokIter.next();
			System.out.println(PokemonList.printPokemonInfo(pok1));
		}
	}
	public static void main(String[] args) throws InterruptedException{
		Battle b = new Battle();
		b.getPokemonOnScreen(false);
	}
}
