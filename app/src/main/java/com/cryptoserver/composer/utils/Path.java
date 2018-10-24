package com.cryptoserver.composer.utils;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.AbstractContainerBox;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {

    private Path() {
    }

    static Pattern component = Pattern.compile("(....|\\.\\.)(\\[(.*)\\])?");

    public static String createPath(Box box) {
        return createPath(box, "");
    }

    private static String createPath(Box box, String path) {

        Container parent = box.getParent();
        int index = 0;
        List<Box> siblings = parent.getBoxes();
        for (Box sibling : siblings) {
            if (sibling.getType().equals(box.getType())) {
                if (sibling == box) {
                    break;
                }
                index++;
            }
        }
        path = String.format("/%s[%d]", box.getType(), index) + path;
        if (parent instanceof Box) {
            return createPath((Box) parent, path);
        } else {
            return path;
        }
    }

    public static <T extends Box> T getPath(Box box, String path) throws Exception {
        List<T> all = getPaths(box, path, true);
        return all.isEmpty() ? null : all.get(0);
    }

    public static <T extends Box> T  getPath(Container container, String path) throws Exception {
        List<T> all = getPaths(container, path, true);
        return all.isEmpty() ? null : all.get(0);
    }

    public static <T extends Box> T  getPath(AbstractContainerBox containerBox, String path) throws Exception {
        List<T> all = getPaths(containerBox, path, true);
        return all.isEmpty() ? null : all.get(0);
    }


    public static <T extends Box> List<T> getPaths(Box box, String path) throws Exception {
        return getPaths(box, path, false);
    }

    public static <T extends Box> List<T> getPaths(Container container, String path) throws Exception {
        return getPaths(container, path, false);
    }

    private static <T extends Box> List<T> getPaths(AbstractContainerBox container, String path, boolean singleResult) throws Exception {
        return getPaths((Object) container, path, singleResult);
    }

    private static <T extends Box> List<T> getPaths(Container container, String path, boolean singleResult) throws Exception {
        return getPaths((Object) container, path, singleResult);
    }

    private static <T extends Box> List<T>  getPaths(Box box, String path, boolean singleResult) throws Exception {
        return getPaths((Object) box, path, singleResult);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Box> List<T>  getPaths(Object thing, String path, boolean singleResult) throws Exception{
        if (path.startsWith("/")) {
            path = path.substring(1);
            while (thing instanceof Box) {
                thing = ((Box) thing).getParent();
            }
        }

        if (path.length() == 0) {
            if (thing instanceof Box) {
                return Collections.singletonList((T) thing);
            }
        } else {
            String later;
            String now;
            if (path.contains("/")) {
                later = path.substring(path.indexOf('/') + 1);
                now = path.substring(0, path.indexOf('/'));
            } else {
                now = path;
                later = "";
            }

            Matcher m = component.matcher(now);
            if (m.matches()) {
                String type = m.group(1);
                if ("..".equals(type)) {
                    if (thing instanceof Box) {
                        return Path.getPaths(((Box) thing).getParent(), later, singleResult);
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    if (thing instanceof Container) {
                        int index = -1;
                        if (m.group(2) != null) {
                            // we have a specific index
                            String indexString = m.group(3);
                            index = Integer.parseInt(indexString);
                        }
                        List<T> children = new LinkedList<T>();
                        int currentIndex = 0;
                        // I'm suspecting some Dalvik VM to create indexed loops from for-each loops
                        // using the iterator instead makes sure that this doesn't happen
                        // (and yes - it could be completely useless)
                        Iterator<Box> iterator = ((Container) thing).getBoxes().iterator();
                        if(iterator != null)
                        {
                            try {
                                while (iterator.hasNext()) {
                                    Box box1 = iterator.next();
                                    if (box1.getType().matches(type)) {
                                        if (index == -1 || index == currentIndex) {
                                            children.addAll(Path.<T>getPaths(box1, later, singleResult));
                                        }
                                        currentIndex++;
                                    }
                                    if ((singleResult || index >= 0) && !children.isEmpty()) {
                                        return children;
                                    }
                                }
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                        return children;
                    }
                }
            }
        }
        return Collections.emptyList();
    }


    public static boolean isContained(Box box, String path) throws Exception {
        assert path.startsWith("/") : "Absolute path required";
        return getPaths(box, path).contains(box);
    }
}

