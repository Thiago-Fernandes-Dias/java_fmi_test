using EasyModbus;
using System.Runtime.InteropServices;

namespace ClassLibrary1
{
    public static class ModbusClientWrapper
    {
        private static ModbusClient ModbusClient = null;

        [UnmanagedCallersOnly(EntryPoint = "startClient")]
        public static void StartClient(IntPtr hostPtr, int port)
        {
            string? host = Marshal.PtrToStringAnsi(hostPtr);
            if (host is null)
            {
                return;
            }
            if (ModbusClient is null)
            {
                ModbusClient = new ModbusClient(host, port);
                ModbusClient.Connect();
            }
            else
            {
                ModbusClient.Disconnect();
                ModbusClient.Connect(host, port);
            }
            Console.WriteLine($"Connect to Modbus server at: {host}");
        }

        public static void Finish()
        {
            ModbusClient?.Disconnect();
        }
    }
}
