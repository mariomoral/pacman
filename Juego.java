import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Juego extends JFrame {
    public static final int FILAS = 31;
    public static final int COLUMNAS = 28;
    public int tamanoCelda;
    public char[][] mapa;
    public Pacman pacman;
    public ArrayList<Enemigo> enemigos;
    public JPanel panelJuego;
    public Timer timer;
    public boolean juegoTerminado = false;

    public Juego() {
        mapa = new char[FILAS][COLUMNAS];
        establecerTamanoCelda();
        generarMapa();
        inicializarInterfaz();
        enemigos = new ArrayList<>();
        crearEnemigos(4);
        iniciarMovimiento();
    }

    private void establecerTamanoCelda() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = (int)(screenSize.height * 0.8);
        int screenWidth = (int)(screenSize.width * 0.8);
        
        int tamanoBasadoEnAltura = screenHeight / FILAS;
        int tamanoBasadoEnAncho = screenWidth / COLUMNAS;
        
        tamanoCelda = Math.min(tamanoBasadoEnAltura, tamanoBasadoEnAncho);
        tamanoCelda = Math.max(tamanoCelda, 15); // Minimo tamaño
    }

    private void generarMapa() {
        String[] mapaDiseno = {
            "############################",
            "#............##............#",
            "#.####.#####.##.#####.####.#",
            "#.####.#####.##.#####.####.#",
            "#.####.#####.##.#####.####.#",
            "#..........................#",
            "#.####.##.########.##.####.#",
            "#.####.##.########.##.####.#",
            "#......##....##....##......#",
            "######.##### ## #####.######",
            "######.##### ## #####.######",
            "######.##          ##.######",
            "######.## ###--### ##.######",
            "######.## #      # ##.######",
            "#     .   #      #   .     #",
            "######.## #      # ##.######",
            "######.## ######## ##.######",
            "######.##          ##.######",
            "######.## ######## ##.######",
            "######.## ######## ##.######",
            "#............##............#",
            "#.####.#####.##.#####.####.#",
            "#.####.#####.##.#####.####.#",
            "#...##.......P........##...#",
            "###.##.##.########.##.##.###",
            "###.##.##.########.##.##.###",
            "#......##....##....##......#",
            "#.##########.##.##########.#",
            "#.##########.##.##########.#",
            "#..........................#",
            "############################"
        };

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                mapa[i][j] = mapaDiseno[i].charAt(j);
                if (mapa[i][j] == 'P') {
                    pacman = new Pacman(j, i);
                    mapa[i][j] = '.';
                }
            }
        }
    }

    private void inicializarInterfaz() {
        setTitle("Pacman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelJuego = new PanelJuego(this);
        JScrollPane scrollPane = new JScrollPane(panelJuego);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pacman.cambiarDireccion(e.getKeyCode());
            }
        });

        // Establecer un tamaño fijo para la ventana
        int width = COLUMNAS * tamanoCelda + 50; // Ancho con un margen
        int height = FILAS * tamanoCelda + 50; // Alto con un margen
        setSize(width, height);
        
        // También puedes establecer un tamaño mínimo
        setMinimumSize(new Dimension(width, height));
        
        setLocationRelativeTo(null);
        setVisible(true);
    }


    private void iniciarMovimiento() {
        timer = new Timer(16, e -> {
            if (!juegoTerminado) {
                pacman.mover(mapa);
                moverEnemigos();
                verificarColision();
                panelJuego.repaint();
            }
        });
        timer.start();
    }

    private void crearEnemigos(int cantidad) {
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            double enemigoX, enemigoY;
            do {
                enemigoX = random.nextInt(COLUMNAS);
                enemigoY = random.nextInt(FILAS);
            } while (mapa[(int)enemigoY][(int)enemigoX] == '#' || 
                     (Math.abs(enemigoX - pacman.getX()) < 5 && Math.abs(enemigoY - pacman.getY()) < 5));
            
            enemigos.add(new Enemigo(enemigoX, enemigoY));
        }
    }

    private void moverEnemigos() {
        for (Enemigo enemigo : enemigos) {
            enemigo.mover(mapa);
        }
    }

    private void verificarColision() {
        for (Enemigo enemigo : enemigos) {
            if (Math.abs(enemigo.x - pacman.getX()) < 0.5 && Math.abs(enemigo.y - pacman.getY()) < 0.5) {
                juegoTerminado = true;
                timer.stop();
                JOptionPane.showMessageDialog(this, "¡Game Over!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }
    }

    public char[][] getMapa() {
        return mapa;
    }

    public int getTamanoCelda() {
        return tamanoCelda;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Juego::new);
    }
}
