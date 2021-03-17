using System;
using System.IO.Ports;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using System.Text;
using System.Threading;
using System.Diagnostics;

namespace NotificationsServer.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class Notify : ControllerBase
    {
        // POST api/<Notify>
        [HttpPost("")]
        public void Post([FromBody] string request)
        {
            // Предположим для начала формат типа "Запрос на блокировку, ФИО, номер телефона, 4 последних цифры номера карты"
            var list = request.Split(',').Select(s => s.Trim()).ToList();
            ProcessStartInfo info = new ProcessStartInfo("cmd.exe");
            string blockRequest = string.Format("/c adb shell am start -a android.intent.action.CALL -d tel:*900*03*{0}*1%23", list[3]);
            info.Arguments = blockRequest;
            Process.Start(info);
        }
    }

}
