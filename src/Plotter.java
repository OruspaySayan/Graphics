/**
 * Created by Sayan on 16.03.2017.
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import static javax.swing.SpringLayout.*;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;

final public class Plotter extends JFrame {
    private static final long serialVersionUID = 1L;
    public static void main(final String[] args) {
        try {
            new Plotter().setVisible(true);
            System.out.print("abs(x)\npow(x, y)\nsin(x)\ncos(x)\n");
        } catch (Throwable e) {
            presentException(e);}
    }
    static void presentException(Throwable t) {
        String title = "Unable to run the " + Plotter.class.getName() + " application.";
        String message = title
                + " \n"
                + "This may be due to a missing tools.jar or missing JFreeChart jars. \n"
                + "Please consult the docs/README file found with this application for further details.";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    public Plotter() {
        super("Function");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(new PlotterPanel());
        pack();
    }
}

final class PlotterPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_FUNCTION = "x * (sin(x) + cos(x))";
    private static final int PAD = 5;
    private final PlotPanel plotPanel = new PlotPanel();
    private final JTextArea errors = new JTextArea();
    private final JTextField plotFunctionText = new JTextField(DEFAULT_FUNCTION, 40);
    public PlotterPanel() {
        Container c = this;
        SpringLayout layout = new SpringLayout();
        c.setLayout(layout);
        JLabel label = new JLabel("f(x)=");
        JButton plotButton = new JButton("Построить график");
        c.add(label);
        c.add(plotFunctionText);
        c.add(plotButton);
        ActionListener plot = new ActionListener() {
            public void actionPerformed(ActionEvent action) {
                generateAndPlotFunction();
            }
        };
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                generateAndPlotFunction();
            }
        });
        plotButton.addActionListener(plot);
        plotFunctionText.addActionListener(plot);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(errors);
        add(plotPanel);
        c.add(scrollPane);
        layout.putConstraint(NORTH, label, PAD, NORTH, c);
        layout.putConstraint(NORTH, plotButton, PAD, NORTH, c);
        layout.putConstraint(NORTH, plotFunctionText, PAD, NORTH, c);
        layout.putConstraint(WEST, label, PAD, WEST, c);
        layout.putConstraint(EAST, plotButton, -PAD, EAST, c);
        layout.putConstraint(WEST, plotFunctionText, PAD, EAST, label);
        layout.putConstraint(EAST, plotFunctionText, -PAD, WEST, plotButton);
        layout.putConstraint(EAST, plotPanel, -PAD, EAST, c);
        layout.putConstraint(WEST, plotPanel, PAD, WEST, c);
        layout.putConstraint(NORTH, plotPanel, PAD, SOUTH, plotButton);
        layout.putConstraint(SOUTH, plotPanel, -PAD, NORTH, scrollPane);
        layout.putConstraint(NORTH, scrollPane, PAD, SOUTH, plotPanel);
        layout.putConstraint(EAST, scrollPane, -PAD, EAST, c);
        layout.putConstraint(WEST, scrollPane, PAD, WEST, c);
        layout.putConstraint(SOUTH, scrollPane, -PAD, SOUTH, c);
        layout.putConstraint(NORTH, scrollPane, -40, SOUTH, c);
        setPreferredSize(new Dimension(800, 600));
    }
    void generateAndPlotFunction() {
        final String source = plotFunctionText.getText();
        length = source.length();
        ch = source.toCharArray();
        final XYSeries series = new XYSeries(source);
        for (int i = -100; i <= 100; i++) {
            x = i / 10.0;
            ip = 0;
            series.add(x, parse());
        }
        final XYDataset xyDataset = new XYSeriesCollection(series);
        boolean legend = false;
        boolean tooltips = true;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createXYLineChart( //
                "f(x)=" + source, // Title
                "x", // X-Axis label
                "f(x)", // Y-Axis label
                xyDataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
        final BufferedImage image = chart.createBufferedImage(plotPanel.getWidth(),
                plotPanel.getHeight());
        final JLabel plotComponent = new JLabel();
        plotComponent.setIcon(new ImageIcon(image));
        plotPanel.image = image;
        plotPanel.repaint();
    }
    static class PlotPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        BufferedImage image;
        @Override
        public void paint(final Graphics g) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            } else {
                g.setColor(Color.lightGray);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    private static char ch[];
    private static int length;
    private static int ip;
    public static double x;
    private static double toDouble(String string) {
        if (string.equals("")) {
            return 0;
        } else {
            char ch[] = string.toCharArray();
            if (ch[0] == 'x' && string.length() == 1) {
                return x;
            } else {
                double data = 0;
                for (int i = 0; i < string.length(); i++) {
                    if (ch[i] >= '0' && ch[i] <= '9') {
                        data += ((int) ch[i] - 48);
                        data *= 10;
                    } else {
                        if (ch[i] == '.') {
                            double d = 0;
                            for (int l = string.length(); i < l; l--) {
                                if (ch[l] >= '0' && ch[l] <= '9') {
                                    d += ((int)ch[i] - 48);
                                    d /= 10;
                                } else {
                                    System.out.println("Функция введена неверно");
                                    System.exit(0);
                                }
                                return data / 10 + d;
                            }
                        } else {
                            System.out.println("Функция введена неверно");
                            System.exit(0);
                        }
                    }
                }
                return data / 10;
            }
        }
    }
    private static double parse() {
        boolean plusOrMinus = true, operator = true, mulOrDiv = true, bracket = true;
        double data = 0, h = 0, f = 0;
        String exp = "";
        for (; ip < length; ip++) {
            if (ch[ip] == '+') {
                if (operator) {
                    if (bracket) {
                        if (plusOrMinus) {
                            data += toDouble(exp);
                        } else {
                            data -= toDouble(exp);
                            plusOrMinus = true;
                        }
                        exp = "";
                    } else {
                        if (plusOrMinus) {
                            data += f;
                        } else {
                            data -= f;
                            plusOrMinus = true;
                        }
                        bracket = true;
                    }
                } else {
                    if (mulOrDiv) {
                        if (bracket) {
                            if (plusOrMinus) {
                                data += h * toDouble(exp);
                            } else {
                                data -= h * toDouble(exp);
                                plusOrMinus = true;
                            }
                            exp = "";
                        } else {
                            if (plusOrMinus) {
                                data += h * f;
                            } else {
                                data -= h * f;
                                plusOrMinus = true;
                            }
                            bracket = true;
                        }
                    } else {
                        if (bracket) {
                            if (plusOrMinus) {
                                data += h / toDouble(exp);
                            } else {
                                data -= h / toDouble(exp);
                                plusOrMinus = true;
                            }
                            exp = "";
                        } else {
                            if (plusOrMinus) {
                                data += h / f;
                            } else {
                                data -= h / f;
                                plusOrMinus = true;
                            }
                            bracket = true;
                        }
                    }
                    h = 0;
                    operator = true;
                }
            } else {
                if (ch[ip] == '-') {
                    if (operator) {
                        if (bracket) {
                            if (plusOrMinus) {
                                data += toDouble(exp);
                                plusOrMinus = false;
                            } else {
                                data -= toDouble(exp);
                            }
                            exp = "";
                        } else {
                            if (plusOrMinus) {
                                data += f;
                                plusOrMinus = false;
                            } else {
                                data -= f;
                            }
                            bracket = true;
                        }
                    } else {
                        if (mulOrDiv) {
                            if (bracket) {
                                if (plusOrMinus) {
                                    data += h * toDouble(exp);
                                    plusOrMinus = false;
                                } else {
                                    data -= h * toDouble(exp);
                                }
                                exp = "";
                            } else {
                                if (plusOrMinus) {
                                    data += h * f;
                                    plusOrMinus = false;
                                } else {
                                    data -= h * f;
                                }
                                bracket = true;
                            }
                        } else {
                            if (bracket) {
                                if (plusOrMinus) {
                                    data += h / toDouble(exp);
                                    plusOrMinus = false;
                                } else {
                                    data -= h / toDouble(exp);
                                }
                                exp = "";
                            } else {
                                if (plusOrMinus) {
                                    data += h / f;
                                    plusOrMinus = false;
                                } else {
                                    data -= h / f;
                                }
                                bracket= true;
                            }
                        }
                        operator = true;
                    }
                } else {
                    if (ch[ip] == '*') {
                        if (bracket) {
                            if (operator) {
                                h = toDouble(exp);
                                operator = false;
                            } else {
                                if (mulOrDiv) {
                                    h *= toDouble(exp);
                                } else {
                                    h /= toDouble(exp);
                                    mulOrDiv = true;
                                }
                            }
                            exp = "";
                        } else {
                            if (operator) {
                                h = f;
                                operator = false;
                            } else {
                                if (mulOrDiv) {
                                    h *= f;
                                } else {
                                    h /= f;
                                    mulOrDiv = true;
                                }
                            }
                            bracket = true;
                        }
                    } else {
                        if (ch[ip] == '/') {
                            if (bracket) {
                                if (operator) {
                                    h = toDouble(exp);
                                    operator = false;
                                } else {
                                    if (mulOrDiv) {
                                        h *= toDouble(exp);
                                        mulOrDiv = false;
                                    } else {
                                        h /= toDouble(exp);
                                    }
                                }
                                exp = "";
                            } else {
                                if (operator) {
                                    h = f;
                                    operator = false;
                                } else {
                                    if (mulOrDiv) {
                                        h *= f;
                                        mulOrDiv = false;
                                    } else {
                                        h /= f;
                                    }
                                }
                                bracket = true;
                            }
                        } else {
                            if (ch[ip] == '(') {
                                ip++;
                                bracket = false;
                                if (exp.equals("")) {
                                    f = parse();
                                } else {
                                    if (exp.equals("sin")) {
                                        f = Math.sin(parse());
                                    } else {
                                        if (exp.equals("cos")) {
                                            f = Math.cos(parse());
                                        } else {
                                            if (exp.equals("abs")) {
                                                f = Math.abs(parse());
                                            } else {
                                                if (exp.equals("pow")) {
                                                    f = Math.pow(parse(),parse());
                                                } else {
                                                    System.out.println("Функция введена неверно");
                                                    System.exit(0);
                                                }
                                            }
                                        }
                                    }
                                    exp = "";
                                }
                            } else {
                                if (ch[ip] == ')') {
                                    if (operator) {
                                        if (bracket) {
                                            if (plusOrMinus) {
                                                data += toDouble(exp);
                                            } else {
                                                data -= toDouble(exp);
                                            }
                                        } else {
                                            if (plusOrMinus) {
                                                data += f;
                                            } else {
                                                data -= f;
                                            }
                                        }
                                    } else {
                                        if (mulOrDiv) {
                                            if (bracket) {
                                                if (plusOrMinus) {
                                                    data += h * toDouble(exp);
                                                } else {
                                                    data -= h * toDouble(exp);
                                                }
                                            } else {
                                                if (plusOrMinus) {
                                                    data += h * f;
                                                } else {
                                                    data -= h * f;
                                                }
                                            }
                                        } else {
                                            if (bracket) {
                                                if (plusOrMinus) {
                                                    data += h / toDouble(exp);
                                                } else {
                                                    data -= h / toDouble(exp);
                                                }
                                            } else {
                                                if (plusOrMinus) {
                                                    data += h / f;
                                                } else {
                                                    data -= h / f;
                                                }
                                            }
                                        }
                                    }
                                    return data;
                                } else {
                                    if (ch[ip] == ',') {
                                        if (operator) {
                                            if (bracket) {
                                                if (plusOrMinus) {
                                                    data += toDouble(exp);
                                                } else {
                                                    data -= toDouble(exp);
                                                }
                                            } else {
                                                if (plusOrMinus) {
                                                    data += f;
                                                } else {
                                                    data -= f;
                                                }
                                            }
                                        } else {
                                            if (mulOrDiv) {
                                                if (bracket) {
                                                    if (plusOrMinus) {
                                                        data += h * toDouble(exp);
                                                    } else {
                                                        data -= h * toDouble(exp);
                                                    }
                                                } else {
                                                    if (plusOrMinus) {
                                                        data += h * f;
                                                    } else {
                                                        data -= h * f;
                                                    }
                                                }
                                            } else {
                                                if (bracket) {
                                                    if (plusOrMinus) {
                                                        data += h / toDouble(exp);
                                                    } else {
                                                        data -= h / toDouble(exp);
                                                    }
                                                } else {
                                                    if (plusOrMinus) {
                                                        data += h / f;
                                                    } else {
                                                        data -= h / f;
                                                    }
                                                }
                                            }
                                        }
                                        ip++;
                                        return data;
                                    } else {
                                        if (ch[ip] != ' ')
                                            exp += ch[ip];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (operator) {
            if (bracket) {
                if (plusOrMinus) {
                    data += toDouble(exp);
                } else {
                    data -= toDouble(exp);
                }
            } else {
                if (plusOrMinus) {
                    data += f;
                } else {
                    data -= f;
                }
            }
        } else {
            if (mulOrDiv) {
                if (bracket) {
                    if (plusOrMinus) {
                        data += h * toDouble(exp);
                    } else {
                        data -= h * toDouble(exp);
                    }
                } else {
                    if (plusOrMinus) {
                        data += h * f;
                    } else {
                        data -= h * f;
                    }
                }
            } else {
                if (bracket) {
                    if (plusOrMinus) {
                        data += h / toDouble(exp);
                    } else {
                        data -= h / toDouble(exp);
                    }
                } else {
                    if (plusOrMinus) {
                        data += h / f;
                    } else {
                        data -= h / f;
                    }
                }
            }
        }
        return data;
    }
}