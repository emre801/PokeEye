package com.johnerdo.battle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.johnerdo.globalInfo.PokemonList;

import com.johnerdo.imageCompare.MatchingMethod;
import com.johnerdo.imageCompare.RobotBot;
import com.johnerdo.pokemonInfo.Pokemon;


public class Battle {

	//LinkedList<String> pokemonOnScreen;
	LinkedList<Pokemon> pokemon;
	public static HashMap<Integer,Pokemon> pokeHash = new HashMap<Integer,Pokemon>();
	public Battle(){
		this.pokemon = new LinkedList<Pokemon>();
		PokemonList.setMapping();
		deserializeHash();
	}
	public ArrayList<Pokemon> getPokemonOnScreen(boolean pushButton) throws InterruptedException{
		if(pushButton){
			RobotBot.Screen();
			Thread.sleep(1500);
		}
		setUp();
		return printPokemon();
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
		movePokemonFromList(pokemonNums);
	}
	
	public void movePokemonFromList(LinkedList<Integer> pokemonNums){
		for(Integer dexNum :pokemonNums){
			if(pokeHash.containsKey(dexNum)){
				pokemon.add(pokeHash.get(dexNum));
				continue;
			}
			Pokemon pokeValue = null;
			if(dexNum<720){
				pokeValue = new Pokemon(dexNum);
			}else{
				pokeValue = new Pokemon(PokemonList.pokemonNames[dexNum]);
			
			}
			System.out.println(pokeValue.toString());
			System.out.println();
			pokemon.add(pokeValue);
			pokeHash.put(dexNum, pokeValue);
		}
		serializeHash();
		MatchingMethod.copyGifsName(pokemonNums);
	}
	
	public ArrayList<Pokemon> printPokemon(){
		Iterator<Pokemon> pokIter = pokemon.iterator();
		ArrayList<Pokemon> pokemonList = new ArrayList<Pokemon>();
		while(pokIter.hasNext()){
			Pokemon pok1 = pokIter.next();
			pokemonList.add(pok1);
			//Pokemon pok2 = pokIter.next();
			System.out.println(PokemonList.printPokemonInfo(pok1));
		}
		return pokemonList;
	}
	public static void main(String[] args) throws InterruptedException{
		Battle b = new Battle();
		b.getPokemonOnScreen(false);
		//pokeListBwahah(b);

	}
	
	public static void pokeListBwahah(Battle b){
		MatchingMethod.copyLocation= "-Mine";
		int[] nums = {1,2,3,4,5,6};
		LinkedList<Integer> pokemonNums = new LinkedList<Integer>();
		for(int i =0;i<nums.length;i++)
			pokemonNums.add(nums[i]);
		b.movePokemonFromList(pokemonNums);
		b.printPokemon();
	}
	
	public static void serializeHash() {
		try {
			FileOutputStream fileOut = new FileOutputStream("PokeHash.txt");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(Pokemon.pokeDataHash);
			out.close();
			fileOut.close();
			System.out.println(Pokemon.pokeDataHash.size());
			System.out.println("Serialized data is saved in BWAHAHAHA PokeHash.txt");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static void deserializeHash() {
		try {
			FileInputStream fileIn = new FileInputStream("PokeHash.txt");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Pokemon.pokeDataHash = (HashMap<Integer, String>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}
	}
	
}
