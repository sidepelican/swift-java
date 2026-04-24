//===----------------------------------------------------------------------===//
//
// This source file is part of the Swift.org open source project
//
// Copyright (c) 2026 Apple Inc. and the Swift.org project authors
// Licensed under Apache License v2.0
//
// See LICENSE.txt for license information
// See CONTRIBUTORS.txt for the list of Swift.org project authors
//
// SPDX-License-Identifier: Apache-2.0
//
//===----------------------------------------------------------------------===//

package com.example.swift;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.swift.swiftkit.core.ClosableSwiftArena;
import org.swift.swiftkit.core.SwiftArena;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 3, jvmArgsAppend = { "--enable-native-access=ALL-UNNAMED" })
public class OptionalBenchmark {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ClosableSwiftArena arena;
        MySwiftClass swiftClass;

        @Setup(Level.Trial)
        public void beforeAll() {
            arena = SwiftArena.ofConfined();
            swiftClass = MySwiftClass.init(arena);
        }

        @TearDown(Level.Trial)
        public void afterAll() {
            arena.close();
        }
    }

    private static final int OPS = 1000;

    @Benchmark
    @OperationsPerInvocation(OPS)
    public void optionalLong(BenchmarkState state, Blackhole bh) {
        for (var i = 0; i < OPS; ++i) {
            bh.consume(MySwiftLibrary.optionalLong(OptionalLong.of(12345L)));
        }
    }

    @Benchmark
    @OperationsPerInvocation(OPS)
    public void optionalString(BenchmarkState state, Blackhole bh) {
        for (var i = 0; i < OPS; ++i) {
            bh.consume(MySwiftLibrary.optionalString(Optional.of("She sells seashells by the seashore.")));
        }
    }

    @Benchmark
    @OperationsPerInvocation(OPS)
    public void optionalClass(BenchmarkState state, Blackhole bh) {
        for (var i = 0; i < OPS; ++i) {
            bh.consume(MySwiftLibrary.optionalClass(Optional.of(state.swiftClass), state.arena));
        }
    }
}
