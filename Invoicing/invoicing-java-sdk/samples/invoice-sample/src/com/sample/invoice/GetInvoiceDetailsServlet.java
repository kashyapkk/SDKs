package com.sample.invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import com.paypal.svcs.services.InvoiceService;
import com.paypal.svcs.types.common.AckCode;
import com.paypal.svcs.types.common.ErrorData;
import com.paypal.svcs.types.common.RequestEnvelope;
import com.paypal.svcs.types.pt.CancelInvoiceRequest;
import com.paypal.svcs.types.pt.CancelInvoiceResponse;
import com.paypal.svcs.types.pt.CreateAndSendInvoiceRequest;
import com.paypal.svcs.types.pt.CreateAndSendInvoiceResponse;
import com.paypal.svcs.types.pt.CreateInvoiceRequest;
import com.paypal.svcs.types.pt.CreateInvoiceResponse;
import com.paypal.svcs.types.pt.GetInvoiceDetailsRequest;
import com.paypal.svcs.types.pt.GetInvoiceDetailsResponse;
import com.paypal.svcs.types.pt.InvoiceItemListType;
import com.paypal.svcs.types.pt.InvoiceItemType;
import com.paypal.svcs.types.pt.InvoiceType;
import com.paypal.svcs.types.pt.PaymentTermsType;

/**
 * Servlet implementation class CreateInvoiceSerlvet
 */
public class GetInvoiceDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetInvoiceDetailsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getServletConfig().getServletContext()
				.getRequestDispatcher("/GetInvoiceDetails.jsp")
				.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		RequestEnvelope env = new RequestEnvelope("en_US");
		GetInvoiceDetailsRequest req = new GetInvoiceDetailsRequest();
		req.setRequestEnvelope(env);
		req.setInvoiceID(request.getParameter("invoiceId"));
		try {

			InvoiceService invoiceSrvc = new InvoiceService(this
					.getServletContext().getRealPath("/")
					+ "/WEB-INF/sdk_config.properties");

			if (request.getParameter("accessToken") != null
					&& request.getParameter("tokenSecret") != null) {
				invoiceSrvc.setAccessToken(request.getParameter("accessToken"));
				invoiceSrvc.setTokenSecret(request.getParameter("tokenSecret"));
			}
			response.setContentType("text/html");
			GetInvoiceDetailsResponse resp = invoiceSrvc.getInvoiceDetails(req);
			response.getWriter().println(
					"Ack:" + resp.getResponseEnvelope().getAck());

			if (resp.getResponseEnvelope().getAck().equals(AckCode.SUCCESS)) {
				response.getWriter().println(
						"<br/>Created By:"
								+ resp.getInvoiceDetails().getCreatedBy());
				response.getWriter().println(
						"<br/>Payment thru PayPal:"
								+ resp.getPaymentDetails().getViaPayPal());
			} else {
				Iterator iterator = resp.getError().iterator();
				while (iterator.hasNext()) {
					ErrorData error = (ErrorData) iterator.next();
					response.getWriter().println("<br/>" + error.getMessage());
				}
			}
			response.getWriter().println("<br/><a href='index.html'>Home</a>");
		} catch (SSLConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientActionRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
