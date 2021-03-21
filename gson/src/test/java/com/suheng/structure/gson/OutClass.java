package com.suheng.structure.gson;

public class OutClass {
    private final String str = "aaaa...aaa";
    private IWriter mWriter;
    private WriterImpl mWriterImpl;
    private WriterStatic mWriterStatic;
    private static WriterStatic mWriterStatic2;

    static {
        setWriterStatic2(new WriterStatic() {
            @Override
            void flush() {
                System.out.println("WriterStatic3, flush");
            }

            @Override
            public void write(String content) {
                System.out.println("WriterStatic3, write: " + content);
            }
        });
    }

    public void setWriter(IWriter writer) {
        mWriter = writer;
    }

    public void setWriterImpl(WriterImpl writerImpl) {
        mWriterImpl = writerImpl;
    }

    public void setWriterStatic(WriterStatic writerStatic) {
        mWriterStatic = writerStatic;
    }

    public static void setWriterStatic2(WriterStatic writerStatic) {
        mWriterStatic2 = writerStatic;
    }

    public void setWriter() {
        setWriter(new IWriter() {
            @Override
            public void write(String content) {
                System.out.println("write: " + content);
            }
        });
    }

    public void setWriterImpl() {
        setWriterImpl(new WriterImpl() {
            @Override
            void flush() {
                System.out.println("WriterImpl: flush, flush");
            }

            @Override
            public void write(String content) {
                System.out.println("WriterImpl, write: " + content);
            }
        });
    }

    public void setWriterStatic() {
        setWriterStatic(new WriterStatic() {
            @Override
            void flush() {
                System.out.println("WriterStatic: flush, flush");
            }

            @Override
            public void write(String content) {
                System.out.println("WriterStatic write: " + content);
            }
        });
    }

    public static void main(String[] args) {
        setWriterStatic2(new WriterStatic() {
            @Override
            void flush() {
                System.out.println("WriterStatic2: flush, flush");
            }

            @Override
            public void write(String content) {
                System.out.println("WriterStatic2, write: " + content);
            }
        });
    }


    interface IWriter {
        void write(String content);
    }

    abstract class WriterImpl implements IWriter {
        private String strWriter = str;

        abstract void flush();
    }

    abstract static class WriterStatic implements IWriter {
        //private String strWriter = str;

        abstract void flush();
    }

}