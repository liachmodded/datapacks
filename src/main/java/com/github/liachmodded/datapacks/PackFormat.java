package com.github.liachmodded.datapacks;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
enum PackFormat {
  // NEW
  // data/modid/advancements
  NAMESPACE_TYPE {
    @Override
    Path getTypeRoot(Path pack) {
      Path data = pack.resolve("data");
      return Files.exists(data) ? data : pack;
    }

    @Override
    Path locateDomain(Path root, String type, String domain) {
      return root.resolve(domain).resolve(type);
    }

    @Override
    Set<String> setupDomains(Path root) throws IOException, UncheckedIOException {
      try (Stream<Path> stream = Files.list(root)) {
        return stream.filter(Files::isDirectory).map(Path::getFileName).map(Path::toString).collect(ImmutableSet.toImmutableSet());
      }
    }
  },
  // OLD
  // data/advancements/modid
  TYPE_NAMESPACE {
    @Override
    Path locateDomain(Path root, String type, String domain) {
      return root.resolve(type).resolve(domain);
    }

    @Override
    Set<String> setupDomains(Path root) throws IOException, UncheckedIOException {
      try (Stream<Path> stream = Files.list(root)) {
        return stream.filter(Files::isDirectory).flatMap(file -> {
          try {
            return Files.list(file);
          } catch (IOException ex) {
            throw new UncheckedIOException(ex);
          }
        }).map(Path::getFileName).map(Path::toString).collect(ImmutableSet.toImmutableSet());
      }
    }
  },
  // UNSURE
  UNSURE {
    @Override
    Path locateDomain(Path root, String type, String domain) {
      Path oldResult = NAMESPACE_TYPE.locateDomain(root, type, domain);
      return Files.exists(oldResult) ? oldResult : TYPE_NAMESPACE.locateDomain(root, type, domain);
    }

    @Override
    Set<String> setupDomains(Path root) throws IOException, UncheckedIOException {
      return ImmutableSet.<String>builder().addAll(NAMESPACE_TYPE.setupDomains(root)).addAll(TYPE_NAMESPACE.setupDomains(root)).build();
    }
  };

  Path getTypeRoot(Path pack) {
    return pack.resolve("data");
  }

  abstract Path locateDomain(Path root, String type, String domain);

  abstract Set<String> setupDomains(Path root) throws IOException, UncheckedIOException;
}
