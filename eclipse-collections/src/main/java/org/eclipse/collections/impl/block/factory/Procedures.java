/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.factory;

import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.block.procedure.CaseProcedure;
import org.eclipse.collections.impl.block.procedure.IfProcedure;
import org.eclipse.collections.impl.block.procedure.checked.CheckedProcedure;
import org.eclipse.collections.impl.block.procedure.checked.ThrowingProcedure;

/**
 * Factory class for commonly used procedures.
 */
public final class Procedures
{
    private Procedures()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Allows a Java 8 lambda and method to be used in a forEach method without requiring a cast.
     */
    public static <T> Procedure<T> cast(Procedure<T> procedure)
    {
        return procedure;
    }

    public static <T> Procedure<T> println(PrintStream stream)
    {
        return new PrintlnProcedure<T>(stream);
    }

    public static <T> Procedure<T> append(Appendable appendable)
    {
        return new AppendProcedure<T>(appendable);
    }

    public static <T> Procedure<T> throwing(ThrowingProcedure<T> throwingProcedure)
    {
        return new ThrowingProcedureAdapter<T>(throwingProcedure);
    }

    /**
     * @deprecated since 1.2 - Inlineable
     */
    @Deprecated
    public static <T> Procedure<T> fromProcedureWithInt(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        return Procedures.fromObjectIntProcedure(objectIntProcedure);
    }

    public static <T> Procedure<T> fromObjectIntProcedure(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        return new ObjectIntProcedureAdapter<T>(objectIntProcedure);
    }

    public static <T> Procedure<T> ifTrue(Predicate<? super T> predicate, Procedure<? super T> block)
    {
        return new IfProcedure<T>(predicate, block);
    }

    public static <T> Procedure<T> ifElse(
            Predicate<? super T> predicate,
            Procedure<? super T> trueProcedure,
            Procedure<? super T> falseProcedure)
    {
        return new IfProcedure<T>(predicate, trueProcedure, falseProcedure);
    }

    public static <T> CaseProcedure<T> caseDefault(Procedure<? super T> defaultProcedure)
    {
        return new CaseProcedure<T>(defaultProcedure);
    }

    public static <T> CaseProcedure<T> caseDefault(
            Procedure<? super T> defaultProcedure,
            Predicate<? super T> predicate,
            Procedure<? super T> procedure)
    {
        return Procedures.caseDefault(defaultProcedure).addCase(predicate, procedure);
    }

    public static <T> Procedure<T> synchronizedEach(Procedure<T> procedure)
    {
        return new Procedures.SynchronizedProcedure<T>(procedure);
    }

    public static <T, P> Procedure<T> bind(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        return new BindProcedure<T, P>(procedure, parameter);
    }

    private static final class PrintlnProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final PrintStream stream;

        private PrintlnProcedure(PrintStream stream)
        {
            this.stream = stream;
        }

        public void value(T each)
        {
            this.stream.println(each);
        }
    }

    private static final class AppendProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;

        private final Appendable appendable;

        private AppendProcedure(Appendable appendable)
        {
            this.appendable = appendable;
        }

        public void value(T each)
        {
            try
            {
                this.appendable.append(String.valueOf(each));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString()
        {
            return this.appendable.toString();
        }
    }

    private static final class ObjectIntProcedureAdapter<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 2L;
        private int count;
        private final ObjectIntProcedure<? super T> objectIntProcedure;

        private ObjectIntProcedureAdapter(ObjectIntProcedure<? super T> objectIntProcedure)
        {
            this.objectIntProcedure = objectIntProcedure;
        }

        public void value(T each)
        {
            this.objectIntProcedure.value(each, this.count);
            this.count++;
        }
    }

    public static final class SynchronizedProcedure<T> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;
        private final Procedure<T> procedure;

        private SynchronizedProcedure(Procedure<T> procedure)
        {
            this.procedure = procedure;
        }

        public void value(T each)
        {
            if (each == null)
            {
                this.procedure.value(null);
            }
            else
            {
                synchronized (each)
                {
                    this.procedure.value(each);
                }
            }
        }
    }

    private static final class BindProcedure<T, P> implements Procedure<T>
    {
        private static final long serialVersionUID = 1L;
        private final Procedure2<? super T, ? super P> procedure;
        private final P parameter;

        private BindProcedure(Procedure2<? super T, ? super P> procedure, P parameter)
        {
            this.procedure = procedure;
            this.parameter = parameter;
        }

        public void value(T each)
        {
            this.procedure.value(each, this.parameter);
        }
    }

    private static final class ThrowingProcedureAdapter<T> extends CheckedProcedure<T>
    {
        private static final long serialVersionUID = 1L;
        private final ThrowingProcedure<T> throwingProcedure;

        private ThrowingProcedureAdapter(ThrowingProcedure<T> throwingProcedure)
        {
            this.throwingProcedure = throwingProcedure;
        }

        public void safeValue(T object) throws Exception
        {
            this.throwingProcedure.safeValue(object);
        }
    }
}
