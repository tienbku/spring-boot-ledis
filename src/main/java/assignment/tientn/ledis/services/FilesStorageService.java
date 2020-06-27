package assignment.tientn.ledis.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.FileSystemUtils;

import assignment.tientn.ledis.exception.FileStorageException;

public class FilesStorageService {

  private final Path dir = Paths.get("ledis-snapshots");

  public FilesStorageService() {
    init();
  }

  public void init() {
    try {
      if (!Files.isDirectory(dir))
        Files.createDirectory(dir);
    } catch (IOException ex) {
      throw new FileStorageException("could not initialize folder for snapshot");
    }
  }

  public void save(String fileName, Object data) {
    final String file = dir.getFileName() + "/" + fileName;

    try (OutputStream os = new FileOutputStream(file);
        OutputStream buffer = new BufferedOutputStream(os);
        ObjectOutput output = new ObjectOutputStream(buffer);) {
      output.writeObject(data);
    } catch (IOException ex) {
      throw new FileStorageException("could not save snapshot");
    }
  }

  public Object load(String file) {
    try (InputStream is = new FileInputStream(file);
        InputStream buffer = new BufferedInputStream(is);
        ObjectInput input = new ObjectInputStream(buffer);) {

      return input.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new FileStorageException("could not read snapshot");
    }
  }

  public List<String> getAllFiles() {
    try (Stream<Path> walk = Files.walk(dir)) {

      List<String> files = walk.map(x -> x.toString()).filter(f -> f.contains("snapshot-"))
          .collect(Collectors.toList());

      return files;
    } catch (IOException e) {
      throw new FileStorageException("error when finding snapshots");
    }
  }

  public boolean deleteAll() {
    boolean deleted = FileSystemUtils.deleteRecursively(dir.toFile());
    try {
      if (!Files.isDirectory(dir))
        Files.createDirectory(dir);
    } catch (IOException ex) {
      throw new FileStorageException("could not initialize folder for snapshot");
    }

    return deleted;
  }

}