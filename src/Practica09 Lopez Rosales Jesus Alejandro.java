import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Caleidoscopio extends JFrame {
    private ArrayList<Shape> baseShapes;
    private Timer timer;
    private Random random;
    private BufferedImage backgroundImage;
    private double rotation = 0;
    private final int SEGMENTS = 12; // Número de segmentos en la mandala
    private final int CENTER_X = 400;
    private final int CENTER_Y = 300;

    public Caleidoscopio() {
        setTitle("Caleidoscopio Mandala");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        baseShapes = new ArrayList<>();
        random = new Random();

        try {
            backgroundImage = ImageIO.read(new File("src/img/fondo.jpg"));
        } catch (IOException e) {
            System.out.println("Error al cargar la imagen de fondo");
        }

        initializeShapes();

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotation += 0.02;
                repaint();
            }
        });
        timer.start();

        add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }

                drawMandala(g2d);
            }
        });
    }

    private void initializeShapes() {
        // Crear formas base que se repetirán en el patrón
        addBaseShapes();
    }

    private void addBaseShapes() {
        // Triángulos
        int[] xPoints = {0, -10, 10};
        int[] yPoints = {-30, -15, -15};
        baseShapes.add(new Polygon(xPoints, yPoints, 3));

        // Rectángulos
        baseShapes.add(new Rectangle2D.Double(-25, -60, 50, 20));

        // Formas adicionales para más complejidad
        baseShapes.add(new Ellipse2D.Double(-8, -80, 16, 16));
        baseShapes.add(new Rectangle2D.Double(-5, -100, 10, 30));

        // Círculos concéntricos
        baseShapes.add(new Ellipse2D.Double(-20, -20, 40, 40));
        baseShapes.add(new Ellipse2D.Double(-15, -15, 30, 30));
    }

    private void drawMandala(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(CENTER_X, CENTER_Y);

        // Dibujar múltiples capas de la mandala
        for (int layer = 0; layer < 3; layer++) {
            double layerRotation = rotation * (layer % 2 == 0 ? 1 : -1);

            for (int segment = 0; segment < SEGMENTS; segment++) {
                double angle = (2 * Math.PI * segment / SEGMENTS) + layerRotation;

                for (Shape baseShape : baseShapes) {
                    // Crear transformación para este segmento
                    AffineTransform transform = new AffineTransform();
                    transform.rotate(angle);
                    transform.scale(1 + layer * 0.5, 1 + layer * 0.5);

                    // Agregar efecto sesgado
                    double shearX = Math.sin(rotation) * 0.5;
                    double shearY = Math.cos(rotation) * 0.5;
                    transform.shear(shearX, shearY);

                    // Efecto de que se aleja y acerca
                    double scale = 1 + Math.sin(rotation) * 0.1;
                    transform.scale(scale, scale);

                    // Aplicar transformación a la forma base
                    Shape transformedShape = transform.createTransformedShape(baseShape);

                    // Color basado en la posición
                    float hue = (float) ((angle + rotation) / (2 * Math.PI));
                    g2d.setColor(Color.getHSBColor(hue, 0.8f, 0.9f));
                    g2d.fill(transformedShape);

                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.draw(transformedShape);
                }
            }
        }

        g2d.setTransform(originalTransform);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Caleidoscopio().setVisible(true);
        });
    }
}