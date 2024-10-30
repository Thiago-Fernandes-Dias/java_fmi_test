using System.Runtime.InteropServices;

namespace ClassLibrary1;

public class Class1
{
    const string GREETING = "Hello there";

    [UnmanagedCallersOnly(EntryPoint = "add")]
    public static int Add(int a, int b) => a + b;

    [UnmanagedCallersOnly(EntryPoint = "subtract")]
    public static int Subtract(int a, int b) => a - b;

    [UnmanagedCallersOnly(EntryPoint = "multiply")]
    public static int Multiply(int a, int b) => a * b;

    // UnmanagedCallersOnly methods only accept primitive arguments. 
    // The primitive arguments have to be marshalled manually if necessary.
    [UnmanagedCallersOnly(EntryPoint = "greet")]
    public static IntPtr Greet(IntPtr namePointer)
    {
        // Parse string from the passed pointer
        // Default to "friend" if a null pointer is passed.
        string name = Marshal.PtrToStringAnsi(namePointer) ?? "friend";

        // Concatenate strings 
        string greeting = $"{GREETING}, {name}";

        // Assign pointer of the concatenated string to sumPointer
        IntPtr sumPointer = Marshal.StringToHGlobalAnsi(greeting);

        // Return pointer
        return sumPointer;
    }

    [UnmanagedCallersOnly(EntryPoint = "listDirs")]
    public static void ListDirs(IntPtr pathPointer)
    {
        string? path = Marshal.PtrToStringAnsi(pathPointer);
        if (path is null)
            return;
        DirectoryInfo[] dirs = new DirectoryInfo(path).GetDirectories();
        using StreamWriter sw = new("dirs.txt");
        foreach (var dir in dirs)
            sw.WriteLine(dir.Name);
    }

    [UnmanagedCallersOnly(EntryPoint = "readFile")]
    public static void ReadFile(IntPtr pathPointer)
    {
        string? path = Marshal.PtrToStringAnsi(pathPointer);
        if (path is null)
            return;
        string content = File.ReadAllText(@$"{path}");
        Console.WriteLine(content);
    }
}