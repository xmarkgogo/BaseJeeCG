package org.jeecg.modules.elfinder.controller.executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.elfinder.controller.executor.AbstractCommandExecutor;
import org.jeecg.modules.elfinder.controller.executor.CommandExecutor;
import org.jeecg.modules.elfinder.service.FsService;

//import com.mortennobel.imagescaling.DimensionConstrain;
//import com.mortennobel.imagescaling.ResampleOp;

public class TmbCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {
	@Override
	public void execute(FsService fsService, HttpServletRequest request,
			HttpServletResponse response, ServletContext servletContext)
			throws Exception
	{


		System.out.println("TmbCommandExecutor"+11);

//		String target = request.getParameter("target");
//		FsItemEx fsi = super.findItem(fsService, target);
//		InputStream is = fsi.openInputStream();
//		BufferedImage image = ImageIO.read(is);
//		int width = fsService.getServiceConfig().getTmbWidth();
//		ResampleOp rop = new ResampleOp(DimensionConstrain.createMaxDimension(
//				width, -1));
//		rop.setNumberOfThreads(4);
//		BufferedImage b = rop.filter(image, null);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ImageIO.write(b, "png", baos);
//		byte[] bytesOut = baos.toByteArray();
//		is.close();
//
//		response.setHeader("Last-Modified",
//				DateUtils.addDays(Calendar.getInstance().getTime(), 2 * 360)
//						.toGMTString());
//		response.setHeader("Expires",
//				DateUtils.addDays(Calendar.getInstance().getTime(), 2 * 360)
//						.toGMTString());
//
//		ImageIO.write(b, "png", response.getOutputStream());
	}
}
