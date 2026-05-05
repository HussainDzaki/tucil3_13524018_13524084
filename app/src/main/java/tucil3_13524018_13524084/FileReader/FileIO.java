package tucil3_13524018_13524084.FileReader;

import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.TileType;
import tucil3_13524018_13524084.Core.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class FileIO {
    public static Board readInput(String inputText) {
        Scanner input = new Scanner(inputText);
        List<Tile> allTiles = new ArrayList<>();
        List<String> barisInputState = new ArrayList<>();
        List<String[]> barisInputCost = new ArrayList<>();

        if (!input.hasNextInt()) {
            input.close();
            throw new IllegalArgumentException("Format peta tidak valid: Baris pertama harus berisi M dan N");
        }
        Integer M = input.nextInt();
        Integer N = input.nextInt();
        input.nextLine();

        for (int i = 0; i < M; i++) {
            if (input.hasNextLine()) {
                barisInputState.add(input.nextLine().replaceAll("\\s+", ""));
            }
        }

        for (int i = 0; i < M; i++) {
            if (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) { // Menghapus Baris Kosong
                    i--;
                    continue;
                }
                barisInputCost.add(line.split("\\s+"));
            }
        }
        Player player = new Player();
        List<Integer> foundCoins = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            String symbols = barisInputState.get(i);
            String[] costs = barisInputCost.get(i);
            if (symbols.length() != N || costs.length != N) {
                input.close();
                throw new IllegalArgumentException("Error Baris ke-" + (i + 1) + "Tidak sesuai dengan ukuran " + N);
            }

            for (int j = 0; j < N; j++) {
                char symbol = symbols.charAt(j);
                Integer value = Integer.parseInt(costs[j]);
                TileType type = parseType(symbol);
                Tile tile;
                if (type == TileType.PLAYER) {
                    player = new Player(j, i);
                    tile = new Tile(j, i, TileType.PATH, value);
                } else {
                    tile = new Tile(j, i, type, value);
                    if (type == TileType.COIN_NUMBER) {
                        int coinVal = Character.getNumericValue(symbol);
                        tile.setCoinSequence(coinVal);
                        foundCoins.add(coinVal);
                    }
                }
                allTiles.add(tile);
            }
        }
        input.close();
        Collections.sort(foundCoins);
        Queue<Integer> coinOrder = new LinkedList<>(foundCoins);
        return new Board(M, N, allTiles, player, coinOrder);

    }

    private static TileType parseType(char c) {
        return switch (c) {
            case 'X' -> TileType.OBSTACLE;
            case 'L' -> TileType.LAVA;
            case 'O' -> TileType.GOAL;
            case 'Z' -> TileType.PLAYER;
            case '*' -> TileType.PATH;
            default -> Character.isDigit(c) ? TileType.COIN_NUMBER : TileType.PATH;
        };
    }
}
