package concrete.goonie.animation;

import concrete.goonie.core.AnimationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationHelper {
    public static void animateTransition(Component oldComponent, Component newComponent,
                                         AnimationType animation, Runnable onComplete) {
        switch (animation) {
            case SLIDE_LEFT:
                slideLeft(oldComponent, newComponent, onComplete);
                break;
            case SLIDE_RIGHT:
                slideRight(oldComponent, newComponent, onComplete);
                break;
            case FADE_IN:
                fadeIn(newComponent, onComplete);
                break;
            case FADE_OUT:
                fadeOut(oldComponent, onComplete);
                break;
            case ZOOM_IN:
                zoomIn(newComponent, onComplete);
                break;
            case ZOOM_OUT:
                zoomOut(oldComponent, onComplete);
                break;
            case NONE:
            default:
                if (onComplete != null) onComplete.run();
                break;
        }
    }

    private static void slideLeft(Component oldComp, Component newComp, Runnable onComplete) {
        // Initial positions
        newComp.setLocation(newComp.getWidth(), 0);
        oldComp.setLocation(0, 0);
        
        animateComponent(newComp, -newComp.getWidth(), 0, 300, onComplete);
        animateComponent(oldComp, 0, -oldComp.getWidth(), 300, null);
    }

    private static void slideRight(Component oldComp, Component newComp, Runnable onComplete) {
        // Initial positions
        newComp.setLocation(-newComp.getWidth(), 0);
        oldComp.setLocation(0, 0);
        
        animateComponent(newComp, newComp.getWidth(), 0, 300, onComplete);
        animateComponent(oldComp, 0, oldComp.getWidth(), 300, null);
    }

    private static void fadeIn(Component comp, Runnable onComplete) {
        comp.setVisible(true);
        animateOpacity(comp, 0f, 1f, 300, onComplete);
    }

    private static void fadeOut(Component comp, Runnable onComplete) {
        animateOpacity(comp, 1f, 0f, 300, () -> {
            comp.setVisible(false);
            if (onComplete != null) onComplete.run();
        });
    }

    private static void zoomIn(Component comp, Runnable onComplete) {
        comp.setSize(0, 0);
        animateSize(comp, comp.getPreferredSize().width, comp.getPreferredSize().height, 
                  300, onComplete);
    }

    private static void zoomOut(Component comp, Runnable onComplete) {
        Dimension original = comp.getSize();
        animateSize(comp, 0, 0, 300, () -> {
            comp.setSize(original);
            if (onComplete != null) onComplete.run();
        });
    }

    private static void animateComponent(Component comp, int startX, int endX, 
                                       int duration, Runnable onComplete) {
        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            private long startTime = -1;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                
                long current = System.currentTimeMillis();
                long elapsed = current - startTime;
                
                if (elapsed >= duration) {
                    comp.setLocation(endX, comp.getY());
                    timer.stop();
                    if (onComplete != null) onComplete.run();
                    return;
                }
                
                float progress = (float) elapsed / duration;
                int currentX = (int) (startX + (endX - startX) * progress);
                comp.setLocation(currentX, comp.getY());
            }
        });
        timer.start();
    }

    private static void animateOpacity(Component comp, float start, float end, 
                                     int duration, Runnable onComplete) {
        if (comp instanceof JComponent) {
            JComponent jcomp = (JComponent) comp;
            jcomp.setOpaque(false);
            
            Timer timer = new Timer(5, null);
            timer.addActionListener(new ActionListener() {
                private long startTime = -1;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (startTime < 0) {
                        startTime = System.currentTimeMillis();
                    }
                    
                    long current = System.currentTimeMillis();
                    long elapsed = current - startTime;
                    
                    if (elapsed >= duration) {
//                        jcomp.putClientProperty("Nimbus.Overrides",
//                            new UIDefaults.LazyValue() {
//                                public Object createValue(UIDefaults table) {
//                                    return new AlphaComposite(end);
//                                }
//                            });
                        timer.stop();
                        if (onComplete != null) onComplete.run();
                        return;
                    }
                    
                    float progress = (float) elapsed / duration;
                    float currentAlpha = start + (end - start) * progress;
                    
                    jcomp.putClientProperty("Nimbus.Overrides", 
                        new UIDefaults.LazyValue() {
                            public Object createValue(UIDefaults table) {
                                return AlphaComposite.getInstance(
                                    AlphaComposite.SRC_OVER, currentAlpha);
                            }
                        });
                    jcomp.repaint();
                }
            });
            timer.start();
        }
    }

    private static void animateSize(Component comp, int targetWidth, int targetHeight, 
                                  int duration, Runnable onComplete) {
        Dimension original = comp.getSize();
        
        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            private long startTime = -1;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                
                long current = System.currentTimeMillis();
                long elapsed = current - startTime;
                
                if (elapsed >= duration) {
                    comp.setSize(targetWidth, targetHeight);
                    timer.stop();
                    if (onComplete != null) onComplete.run();
                    return;
                }
                
                float progress = (float) elapsed / duration;
                int currentWidth = (int) (original.width + (targetWidth - original.width) * progress);
                int currentHeight = (int) (original.height + (targetHeight - original.height) * progress);
                comp.setSize(currentWidth, currentHeight);
                comp.revalidate();
            }
        });
        timer.start();
    }
}