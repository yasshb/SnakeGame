import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    //Classe interne pour représenter une case (Tile) du serpent ou de la nourriture
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25; // Taille d'une case

    // Serpent
    Tile snakeHead; // Tête du serpent
    ArrayList<Tile> snakeBody; // Corps du serpent

    // Nourriture
    Tile food; // Nourriture
    Random random; // Générateur de nombres aléatoires

    // Logique du jeu
    int velocityX; // Vitesse en X
    int velocityY; // Vitesse en Y
    Timer gameLoop; // Boucle de jeu

    boolean gameOver = false; // Indicateur de fin de jeu

    // Constructeur du jeu
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this); // Ajout du listener pour les touches du clavier
        setFocusable(true);

        // Initialisation de la tête et du corps du serpent
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        // Initialisation de la nourriture
        food = new Tile(10, 10);
        random = new Random();
        placeFood(); // Placer la nourriture aléatoirement

        velocityX = 1; // Déplacement initial en X
        velocityY = 0; // Pas de déplacement initial en Y

        // Timer du jeu
        gameLoop = new Timer(100, this); // Intervalle de temps entre les frames (en millisecondes)
        gameLoop.start(); // Démarrer le jeu
    }

    // Méthode pour dessiner les composants du jeu
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Méthode pour dessiner le jeu
    public void draw(Graphics g) {
        // Lignes de la grille
        for(int i = 0; i < boardWidth/tileSize; i++) {
            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
            g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        }

        // Nourriture
        g.setColor(Color.red);
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);

        // Tête du serpent
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);

        // Corps du serpent
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    // Méthode pour placer la nourriture aléatoirement
    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    // Méthode pour déplacer le serpent
    public void move() {
        // Manger la nourriture
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y)); // Ajouter un segment au serpent
            placeFood(); // Placer une nouvelle nourriture
        }

        // Déplacer le corps du serpent
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { // Juste avant la tête
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Déplacer la tête du serpent
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Conditions de fin de jeu
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            // Collision avec la tête du serpent
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        // Collision avec les bords du plateau
        if (snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || // Passé la bordure gauche ou droite
                snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight ) { // Passé la bordure supérieure ou inférieure
            gameOver = true;
        }
    }

    // Méthode pour vérifier la collision entre deux cases
    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    // Méthode appelée à chaque intervalle de temps par le timer du jeu
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // Méthode pour gérer les pressions de touches du clavier
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    // Méthodes inutiles mais nécessaires pour l'interface KeyListener
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
